package com.medium.binding.src.main.home.create_room

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.*
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.DialogFragment
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.medium.binding.R
import com.medium.binding.config.ApplicationClass
import com.medium.binding.config.BaseResponse
import com.medium.binding.src.main.home.create_room.models.CreateBookBody
import com.medium.binding.util.LoadingDialog
import java.io.File


class CreateBookDialog(context: Context) : DialogFragment(), CreateBookView {
    lateinit var mLoadingDialog: LoadingDialog

    private val IMAGE_CHOOSE = 1000
    private val PERMISSION_CODE = 1001

    private var db = ApplicationClass.userStorage.reference.child("test2")

    private var imgPath: String? = null
    private var storageUrl: String? = null

    lateinit var bookName: EditText             // 책 제목
    lateinit var author: EditText               // 저자
    private lateinit var btnAddImg: ImageView   // 이미지 추가 버튼
    private lateinit var imgAdded: ImageView    // 이미지가 추가될 이미지뷰
    private lateinit var guideTxt: TextView     // 표지추가 기본 문구
    private lateinit var wrongTxt1: TextView    // 제목 에러문구
    private lateinit var wrongTxt2: TextView    // 저자 에러문구
    private lateinit var wrongTxt3: TextView    // 표지 에러문구

    private var isImgAdded = false

    // 다이얼로그 사이즈 설정
    override fun onResume() {
        super.onResume()

        // 다이얼로그 크기 설정
        val params: WindowManager.LayoutParams = dialog?.window?.attributes!!
        val widthSize = ((activity!!.resources.displayMetrics.widthPixels) * 0.88).toInt()
        // val heightSize = ((activity!!.resources.displayMetrics.heightPixels) * 0.55).toInt()
        val density: Float = activity!!.resources.displayMetrics.density   // 기기 density
        val maxWidthPx = (360 * density + 0.5).toInt()      // 360dp -> 픽셀 변환
        val maxHeightPx = (470 * density + 0.5).toInt()     // 470dp -> 픽셀 변환

        // 스크린 너비의 88%가 360dp보다 크면 360dp로 고정
        if(widthSize > maxWidthPx){
            params.width = maxWidthPx
        }else{
            params.width = widthSize
        }
        // 스크린 높이 470dp로 고정
        params.height = maxHeightPx
        dialog?.window?.attributes = params
    }

    // 테두리 적용
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_create_room, container, false)
        //다이얼로그의 꼭짓점이 짤리는부분 방지.
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // dialog?.setCanceledOnTouchOutside(true)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookName = view.findViewById<EditText>(R.id.dialog_create_room_title)
        author = view.findViewById<EditText>(R.id.dialog_create_room_author)
        btnAddImg = view.findViewById<ImageView>(R.id.dialog_create_room_add)
        imgAdded = view.findViewById<ImageView>(R.id.dialog_create_room_added)
        guideTxt = view.findViewById<TextView>(R.id.dialog_create_room_guide)
        wrongTxt1 = view.findViewById<TextView>(R.id.dialog_create_room_title_wrong)
        wrongTxt2 = view.findViewById<TextView>(R.id.dialog_create_room_author_wrong)
        wrongTxt3 = view.findViewById<TextView>(R.id.dialog_create_room_guide_wrong)
        val titleLine = view.findViewById<View>(R.id.dialog_create_room_title_line)
        val authorLine = view.findViewById<View>(R.id.dialog_create_room_author_line)

        // 만들기 버튼 클릭
        val btnCreate = view.findViewById<Button>(R.id.dialog_create_room_btn)
        btnCreate.setOnClickListener {
            val bookNameStr = bookName.text.toString()
            val authorStr = author.text.toString()
            Log.d("로그", "bookNameStr: $bookNameStr , authorStr: $authorStr ," +
                    " storageUrl: $storageUrl"
            )

            if(bookNameStr.isBlank()){
                wrongTxt1.visibility = View.VISIBLE
                bookName.visibility = View.INVISIBLE
            }
            if(authorStr.isBlank()){
                wrongTxt2.visibility = View.VISIBLE
                author.visibility = View.INVISIBLE
            }
            if(!isImgAdded){
                wrongTxt3.visibility = View.VISIBLE
                guideTxt.visibility = View.INVISIBLE
            }

            if(bookNameStr.isNotBlank() && authorStr.isNotBlank() && isImgAdded){
                showLoadingDialog(context!!)
                val mThread = Thread(Runnable{
                    storeAndCall()
                })
                mThread.run()
            }
        }

        // 첫번째 editText칸 엔터키 -> 아래 editText로 이동
        bookName.setOnKeyListener { _, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                author.performClick()
            }
            false
        }

        // 2번째 editText 엔터버튼 클릭 -> 표지 추가 버튼 포커스
        author.setOnKeyListener { _, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_ENDCALL || keyCode == KeyEvent.KEYCODE_ENTER)
            ){
                if(!isImgAdded){
                    btnAddImg.requestFocus()
                }else{
                    btnAddImg
                }
            }
            false
        }

        // 에딧 텍스트 클릭과 유사 역할
        titleLine.setOnClickListener{
            if(wrongTxt1.visibility == View.VISIBLE){
                wrongTxt1.performClick()
            }else{
                bookName.performClick()
            }
        }
        authorLine.setOnClickListener {
            if(wrongTxt2.visibility == View.VISIBLE){
                wrongTxt2.performClick()
            }else{
                author.performClick()
            }
        }

        // 책 표지 추가 버튼
        btnAddImg.setOnClickListener(onClickAdd)
        imgAdded.setOnClickListener(onClickAdd) // 표지 클릭(변경) 버튼

        // 다시 입력하기 위해 에러문구를 클릭할 때
        wrongTxt1.setOnClickListener(onClickWrong1)
        wrongTxt2.setOnClickListener(onClickWrong2)
    }

    // 책 제목 에러 문구 클릭 -> 에러문구 사라지고, 다시 입력창이 나타난다
    private val onClickWrong1 = View.OnClickListener {
        bookName.visibility = View.VISIBLE
        it.visibility = View.INVISIBLE
        bookName.performClick()
    }

    // 저자 에러 문구 클릭 -> 에러문구 사라지고, 다시 입력창이 나타난다
    private val onClickWrong2 = View.OnClickListener {
        author.visibility = View.VISIBLE
        it.visibility = View.INVISIBLE
        author.performClick()
    }

    // 갤러리에서 사진 선택
    private val onClickAdd = View.OnClickListener {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // 갤러리 접근 권한이 없으면 권한을 요청
            if (activity!!.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissions(permissions, PERMISSION_CODE)
            }
            // 접근 권한이 있으면 chooseImg() 실행
            else{
                chooseImg();
            }
        }else{
            chooseImg();
        }
    }

    // 파이어베이스에 이미지 저장하고 이미지 변경 API 호출
    private fun storeAndCall(){
        val file = Uri.fromFile(File(imgPath!!))
        val riversRef = db.child("bookImg")
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
                storageUrl = task.result.toString()   // 이미지 다운로드가 가능한 url

                // 책방 만들기 API call
                CreateBookService(this).tryPostBook(
                    CreateBookBody(bookName.text.toString(),
                        author.text.toString(), storageUrl!!
                    )
                )
                Log.d("로그", "storageUrl: $storageUrl")
            } else {
                dismissLoadingDialog()
                Toast.makeText(activity, "책 사진을 업로드하는 과정에서 오류가 발생했습니다.\n" +
                        "오류가 계속되면 관리자에게 문의해주세요.", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(activity, "갤러리 접근 권한이 거부되었습니다",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 갤러리 이미지 경로를 절대 경로로 변환
    // Q이하에서는 비트맵으로 변환 후 이미지뷰에 적용
    // Q이상에서는 URI를 이미지뷰에 적용
    private fun renderGalleyImg(imgUri: Uri){
        imgPath = getRealPathFromURI(imgUri) ?: return
        Log.d("로그","imgPath: $imgPath")

        // 버전 Q 이상부터는 저장소를 읽고 쓰는데 제한이 있다고 한다...
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATE_TAKEN
            )

            activity!!.contentResolver.query(imgUri, projection, null, null
            )?.let {
                with(it) {
                    if(moveToFirst()) {
                        val id = getLong(getColumnIndex(MediaStore.Images.Media._ID))
                        val contentUri = Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString()
                        )
                        Glide.with(context!!)
                            .load(contentUri)
                            .error(R.drawable.add)
                            .into(imgAdded)

                        isImgAdded = true
                        btnAddImg.visibility = View.INVISIBLE
                        imgAdded.visibility = View.VISIBLE
                        guideTxt.visibility = View.GONE
                        wrongTxt3.visibility = View.GONE
                    }
                    close()
                }
            } ?:run {
                Toast.makeText(activity, "사진이 없습니다",
                    Toast.LENGTH_SHORT).show()
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
                .load(imgPath)
                .error(R.drawable.add)
                .into(imgAdded)

            isImgAdded = true
            imgAdded.visibility = View.VISIBLE
            btnAddImg.visibility = View.INVISIBLE
            guideTxt.visibility = View.GONE
            wrongTxt3.visibility = View.GONE
        }
    }

    //img uri -> filePath 변환
    private fun getRealPathFromURI(imgUri: Uri):String? {
        val project = arrayOf(MediaStore.Images.Media.DATA)
        val cursorLoader = CursorLoader(
            context!!, imgUri, project, null, null, null
        )

        val cursor: Cursor? = cursorLoader.loadInBackground()
        if(cursor != null ){
            val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(index)
        }
        return null
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

    // 갤러리 접근 결과 콜백
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 갤러리에서 사진을 골랐을 때
        if(requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK){
            Log.d("로그", "IMAGE_CHOOSE - data?.data: ${data?.data}")

            // 이미지뷰에 출력
            if(data != null && data.data != null){
                renderGalleyImg(data.data!!)
            }
        }
    }

    override fun onPostBookSuccess(response: BaseResponse) {
        Log.d("로그", "onPostBookSuccess() called, response: $response")
        dismissLoadingDialog()

        when(response.code){

            // 성공
            1000 -> {
                showCustomToast("책방 만들기 성공!")
                dismiss()
            }

            // 책 제목을 입력해주세요
            2000 -> {
                wrongTxt1.visibility = View.VISIBLE
                bookName.visibility = View.INVISIBLE
            }

            // 저자를 입력해주세요
            2001 -> {
                wrongTxt2.visibility = View.VISIBLE
                author.visibility = View.INVISIBLE
            }

            // 책 표지 사진을 첨부해주세요.
            2002 -> {
                wrongTxt3.visibility = View.VISIBLE
                guideTxt.visibility = View.INVISIBLE
            }

            // 하루에 6개 이상 책방을 등록할 수 없습니다.
            3000 -> {
                showCustomToast(response.message ?: "하루에 6개 이상 책방을 등록할 수 없습니다.")
                dismiss()
            }

            // 해당 책방이 이미 존재합니다.
            3001 -> showCustomToast(response.message?: "해당 책방이 이미 존재합니다.")

        }
    }

    override fun onPostBookFailure(message: String) {
        Log.d("로그", "onPostBookFailure() called, message: $message")
        dismissLoadingDialog()

        showCustomToast("책방을 만들던 중 에러가 발생했습니다.\n에러가 계속되면 관리자에게 문의해주세요.")
    }

    fun showLoadingDialog(context: Context) {
        mLoadingDialog = LoadingDialog(context)
        mLoadingDialog.show()
    }

    fun dismissLoadingDialog() {
        if (mLoadingDialog.isShowing) {
            mLoadingDialog.dismiss()
        }
    }

    fun showCustomToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}