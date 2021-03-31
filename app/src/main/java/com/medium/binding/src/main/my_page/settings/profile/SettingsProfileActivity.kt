package com.medium.binding.src.main.my_page.settings.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.exifinterface.media.ExifInterface
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseActivity
import com.medium.binding.config.BaseResponse
import com.medium.binding.databinding.ActivitySettingsProfileBinding
import com.medium.binding.src.main.my_page.settings.models.GetProfileResponse
import com.medium.binding.src.main.my_page.settings.models.PatchProfileBody
import com.medium.binding.util.JoinClass
import java.io.File


class SettingsProfileActivity : BaseActivity<ActivitySettingsProfileBinding>(
    ActivitySettingsProfileBinding::inflate
), SettingsProfileActivityView {

    companion object{
        private const val IMAGE_CHOOSE = 1000
        private const val PERMISSION_CODE = 1001
        private const val IMG_SAME = 0
        private const val IMG_ERASED = 1
        private const val IMG_CHANGED = 2
    }

    // 버튼 중복클릭 방지용
    private var mLastClickTime: Long = 0

    private var isImgOn = false             // 이미지가 있는지 없는지 플래그
    private var imgPath: String? = null     // 갤러리 이미지 절대 경로
    private var imgFlag: Int = IMG_SAME     // 이미지 변경 플래그

    private lateinit var nickname: String

    private lateinit var imgDialog: ChangeImageDialog   // 이미지 눌렀을 때 나오는 다이얼로그
    private var db = ApplicationClass.userStorage.reference.child("user${ApplicationClass.userIdx}")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 프로필 정보를 불러온다
        showLoadingDialog(this)
        SettingsProfileService(this).tryGetProfile()

        // 뒤로가기 버튼
        binding.settingsProfileLeft.setOnClickListener {
            finish()
        }

        // 이미지 버튼 -> 변경 or 삭제
        binding.settingsProfilePhoto.setOnClickListener{
            imgDialog = ChangeImageDialog(this, removeImage, chooseImage, isImgOn)
            imgDialog.show()
        }

        // 완료 버튼
        binding.settingsProfileDone.setOnClickListener(onClickDone)

        // 닉네임 완료버튼 클릭 -> 완료 버튼 자동 눌림
        binding.settingsProfileNickname.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENDCALL || keyCode == KeyEvent.KEYCODE_ENTER)
            ){
                binding.settingsProfileDone.performClick()
            }
            false
        }

    }

    private val onClickDone = View.OnClickListener {
        showLoadingDialog(this)

        // 중복 클릭 방지
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return@OnClickListener
        }
        mLastClickTime = SystemClock.elapsedRealtime()

        val nicknameTxt = binding.settingsProfileNickname.text.toString()
        // 닉네임 변경 x
        if(nickname == nicknameTxt){
            when(imgFlag){

                // 닉변x , 이미지 수정사항 x -> 그냥 수정화면에서 나간다
                IMG_SAME -> {
                    dismissLoadingDialog()
                    finish()
                }

                // 닉변x , 이미지 삭제
                IMG_ERASED -> {
                    SettingsProfileService(this)
                        .tryPatchProfile(PatchProfileBody("-1", null))
                }

                // 닉변x , 이미지 변경
                IMG_CHANGED -> storeAndCall(editedNick = null)
            }
        }
        // 닉네임 변경 o
        else{

            // 바꿀 닉네임 형식이 올바르지 않으면, 에러문구 출력
            if(!JoinClass.isValidNickname(nicknameTxt)){
                dismissLoadingDialog()
                binding.settingsProfileWrong.apply{
                    text = String.format("닉네임 형식이 맞지 않습니다")
                    visibility = View.VISIBLE
                }
            }
            // 바꿀 닉네임 형식이 올바를 때
            else{
                when(imgFlag){

                    // 닉네임만 변경 요청
                    IMG_SAME ->  {
                        SettingsProfileService(this)
                            .tryPatchProfile(PatchProfileBody(null, nicknameTxt))
                    }

                    // 닉변o , 이미지 삭제
                    IMG_ERASED -> {
                        SettingsProfileService(this)
                            .tryPatchProfile(PatchProfileBody("-1", nicknameTxt))
                    }

                    // 닉변o, 이미지 변경
                    IMG_CHANGED -> storeAndCall(editedNick = nicknameTxt)

                }
            }
        }
    }

    // 이미지 삭제 버튼 리스너 -> 삭제된 걸 보여주고,
    // 실제 삭제는 완료 버튼을 클릭했을 때 발생
    private val removeImage = View.OnClickListener {
        imgFlag = IMG_ERASED
        isImgOn = false
        binding.settingsProfilePhoto.setImageResource(R.drawable.icon_app)
        imgDialog.dismiss()
    }

    // 갤러리에서 사진 선택
    private val chooseImage = View.OnClickListener {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // 갤러리 접근 권한이 없으면 권한을 요청
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            }
            // 접근 권한이 있으면 chooseImg() 실행
            else{
                chooseImg()
            }
        }else{
            chooseImg()
        }
    }

    // 파이어베이스에 이미지 저장하고 프로필 변경 API 호출
    private fun storeAndCall(editedNick: String?){
        val file = Uri.fromFile(File(imgPath!!))
        val riversRef = db.child("profileImg")
        val uploadTask = riversRef.putFile(file)

        // 파이어베이스에 이미지 업로드
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    dismissLoadingDialog()
                    throw it
                }
            }
            // 업로드한 url 다운로드
            riversRef.downloadUrl
        }.addOnCompleteListener { task ->           // 업로드 성공 -> 이미지 변경 api 실행
            if (task.isSuccessful) {
                val downloadUrl = task.result   // 이미지 다운로드가 가능한 url
                SettingsProfileService(this)
                    .tryPatchProfile(PatchProfileBody(downloadUrl.toString(), editedNick))

            } else {
                dismissLoadingDialog()
                showCustomToast(
                    "프로필 사진을 바꾸는 과정에서 오류가 발생했습니다.\n" +
                            "오류가 계속되면 관리자에게 문의해주세요."
                )
            }
        }
    }

    // 갤러리 인텐트 호출
    private fun chooseImg(){
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGE_CHOOSE)
    }

    // 접근권한 요청 결과 콜백
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImg()
                } else {
                    showCustomToast("접근 권한이 거부되었습니다")
                    imgDialog.dismiss()
                }
            }
        }
    }

    // 갤러리 이미지 경로를 절대 경로로 변환
    // Q이하에서는 비트맵으로 변환 후 이미지뷰에 적용
    // Q이상에서는 URI를 이미지뷰에 적용
    private fun renderGalleyImg(imgUri: Uri){
        imgPath = getRealPathFromURI(imgUri) ?: return

        // 버전 Q 이상부터는 저장소를 읽고 쓰는데 제한이 있다고 한다...
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN
            )

            contentResolver.query(imgUri, projection, null, null)?.let {
                with(it) {
                    if(moveToFirst()) {
                        val id = getLong(getColumnIndex(MediaStore.Images.Media._ID))
                        val contentUri = Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString()
                        )
                        Glide.with(this@SettingsProfileActivity)
                            .load(contentUri)
                            .into(binding.settingsProfilePhoto)
                        imgFlag = IMG_CHANGED
                        isImgOn = true
                    }
                    close()
                }
            } ?:run {
                showCustomToast("사진이 없습니다")
                Glide.with(this)
                    .load(R.drawable.icon_app)
                    .into(binding.settingsProfilePhoto)
            }
        }
        // Q 이하 버전
        else{
            var bitmap: Bitmap = BitmapFactory.decodeFile(imgPath)  //경로를 통해 비트맵으로 전환

            // 회전 각도 값을 가져온다.
            val degree: Float = getDegree(imgPath)
            // 정방향 회전
            bitmap = rotate(bitmap, degree)

            //이미지 뷰에 비트맵 넣기
            Glide.with(this)
                .asBitmap()
                .load(bitmap)
                .into(binding.settingsProfilePhoto)

            // 플래그에 이미지가 변경됐음을 반영
            imgFlag = IMG_CHANGED
            isImgOn = true
        }
    }


    private fun getDegree(source: String?): Float {
        try {
            val exif = source?.let { ExifInterface(it) }
            var degree = 0
            val ori: Int = exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1)!!
            when (ori) {
                ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
                ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
                ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            }
            return degree.toFloat()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0.0f
    }

    private fun rotate(src:Bitmap, degree:Float):Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    //img uri -> filePath 변환
    private fun getRealPathFromURI(imgUri: Uri):String? {
        val project = arrayOf(MediaStore.Images.Media.DATA)
        val cursorLoader = CursorLoader(
            this, imgUri, project, null, null, null
        )

        val cursor: Cursor? = cursorLoader.loadInBackground()
        if(cursor != null ){
            val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(index)
        }
        return null
    }

    // 갤러리 접근 결과 콜백
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 갤러리에서 사진을 골랐을 때
        if(requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK){

            // 이미지뷰에 출력
            if(data != null && data.data != null){
                renderGalleyImg(data.data!!)
            }
        }

        imgDialog.dismiss()
    }

    // 프로필 조회 통신 성공
    override fun onGetProfileSuccess(response: GetProfileResponse) {
        dismissLoadingDialog()

        when(response.code){

            // 프로필 조회 성공 -> 프로필 정보를 보여준다
            1000 -> {
                if (response.result.size > 0) {
                    val result = response.result[0]
                    nickname = result.nickname
                    binding.settingsProfileNickname.setText(nickname)
                    binding.settingsProfileEmail.text = result.email

                    // 프로필이미지가 있으면 true, 없으면 false
                    isImgOn = result.userImgUrl != "-1"
                    Glide.with(this)
                        .load(result.userImgUrl)
                        .error(R.drawable.icon_app)
                        .placeholder(R.drawable.icon_app)
                        .into(binding.settingsProfilePhoto)
                }
            }
            else -> {
                showCustomToast("프로필 조회 중 오류가 발생했습니다," +
                        " 오류가 계속되면 관리자에게 문의주세요.")
                finish()
            }
        }
    }

    // 프로필 조회 통신 실패
    override fun onGetProfileFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("프로필 조회 중오류가 발생했습니다\n" +
                "네트워크 확인 후 오류가 계속되면 관리자에게 문의주세요.")
    }

    // 프로필 변경 통신 성공
    override fun onPatchProfileSuccess(response: BaseResponse) {
        dismissLoadingDialog()

        when(response.code){

            // 성공
            1000 -> {
                showCustomToast("프로필이 변경되었습니다")
                ApplicationClass.isEdited = true
                finish()
            }

            // 닉네임 형식 오류
            in 2001..2002 ->{
                binding.settingsProfileWrong.apply{
                    text = String.format("닉네임 형식이 맞지 않습니다")
                    visibility = View.VISIBLE
                }
            }

            // 사용중 닉네임
            3001 -> {
                binding.settingsProfileWrong.apply{
                    text = String.format("이미 사용중인 닉네임입니다")
                    visibility = View.VISIBLE
                }
            }

            else -> showCustomToast("프로필 변경 중 오류가 발생했습니다, " +
                    "오류가 계속되면 관리자에게 문의해주세요.")
        }
    }

    // 프로필 변경 통신 실패
    override fun onPatchProfileFailure(message: String) {
        dismissLoadingDialog()

        showCustomToast("프로필 변경 중 오류가 발생했습니다\n" +
                "네트워크 확인 후 오류가 반복되면 관리자에게 문의주세요.")
    }
}