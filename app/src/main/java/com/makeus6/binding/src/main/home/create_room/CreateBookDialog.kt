package com.makeus6.binding.src.main.home.create_room

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.DialogFragment
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.makeus6.binding.R
import com.makeus6.binding.config.ApplicationClass
import com.makeus6.binding.src.main.my_page.settings.models.PatchImgBody
import com.makeus6.binding.src.main.my_page.settings.profile.SettingsProfileActivity
import com.makeus6.binding.src.main.my_page.settings.profile.SettingsProfileService
import java.io.File


class CreateBookDialog(context: Context) : DialogFragment() {

    private val IMAGE_CHOOSE = 1000
    private val PERMISSION_CODE = 1001

    private var db = ApplicationClass.userStorage.reference.child("test2")

    private var imgPath: String? = null
    private var storageUrl: String? = null

    lateinit var bookName: EditText
    lateinit var author: EditText
    private lateinit var btnAdd: ImageView


    override fun onResume() {
        super.onResume()

        /*// 꼭 DialogFragment 클래스에서 선언하지 않아도 된다.
        val windowManager = activity!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)*/

        // 다이얼로그 크기 설정
        val params: WindowManager.LayoutParams = dialog?.window?.attributes!!
        val widthSize = ((activity!!.resources.displayMetrics.widthPixels) * 0.88).toInt()
        val heightSize = ((activity!!.resources.displayMetrics.heightPixels) * 0.55).toInt()
        val density: Float = activity!!.resources.displayMetrics.density   // 기기 density
        val maxWidthPx = (360 * density + 0.5).toInt()      // 360dp -> 픽셀 변환
        val maxHeightPx = (470 * density + 0.5).toInt()     // 470dp -> 픽셀 변환
        Log.d("로그","widthSize: $widthSize , heightSize: $heightSize , maxWidthPx: $maxWidthPx , maxHeightPx: $maxHeightPx")
        Log.d("로그", "dialog size: ${dialog?.window!!.attributes.width},${dialog?.window!!.attributes.height} ")
        // 스크린 너비의 88%가 360dp보다 크면 360dp로 고정
        if(widthSize > maxWidthPx){
            params.width = maxWidthPx
        }else{
            params.width = widthSize
        }
        // 스크린 높이의 55%가 470dp보다 크면 470dp로 고정
        if(heightSize > maxHeightPx){
            params.height = maxHeightPx
        }else{
            params.height = heightSize
        }
        dialog?.window?.attributes = params
        Log.d("로그", "dialog size2: ${dialog?.window!!.attributes.width},${dialog?.window!!.attributes.height} ")
    }

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

        // 만들기 버튼 클릭
        val btn = view.findViewById<Button>(R.id.dialog_create_room_btn)
        btn.setOnClickListener {
            dismiss()
        }

        bookName = view.findViewById<EditText>(R.id.dialog_create_room_title)
        author = view.findViewById<EditText>(R.id.dialog_create_room_author)
        btnAdd = view.findViewById<ImageView>(R.id.dialog_create_room_add)

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
                btnAdd.requestFocus()
            }
            false
        }

        btnAdd.setOnClickListener(onClickAdd)
    }

    // 갤러리에서 사진 선택
    private val onClickAdd = View.OnClickListener {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            // 갤러리 접근 권한이 없으면 권한을 요청
            if (activity!!.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
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
                    throw it
                }
            }
            // 업로드한 url 다운로드
            riversRef.downloadUrl
        }.addOnCompleteListener { task ->           // 업로드 성공 -> 이미지 변경 api 실행
            if (task.isSuccessful) {
                storageUrl = task.result.toString()   // 이미지 다운로드가 가능한 url

                Log.d("로그", "storageUrl: $storageUrl")
            } else {
                Toast.makeText(activity, "책 사진을 올리는 과정에서 오류가 발생했습니다.\n" +
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
                            .into(btnAdd)
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
                .into(btnAdd)
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
}