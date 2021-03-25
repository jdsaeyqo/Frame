package com.example.frame

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var imageViewList = listOf<ImageView>()

    private val imageUriList = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewList =
            listOf(imageView1, imageView2, imageView3, imageView4, imageView5, imageView6)


        initAddPhoto()
        initStartFrame()
    }


    private fun initAddPhoto() {
        addPhotoBtn.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    //권한이 부여되었을 때 갤러리 열기
                    openGallery()

                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    //교육용 팝업 확인 후 권한 팝업 띄우는 기능
                    showPermissionContextPopup()

                }

                else -> {
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }

            }
        }
    }

    private fun initStartFrame() {

        startFrameBtn.setOnClickListener {
            val intent = Intent(this,PhotoFrameActivity::class.java)
            imageUriList.forEachIndexed { index, uri ->
                intent.putExtra("photo$index",uri.toString())
            }
            intent.putExtra("photoListSize",imageUriList.size)

            startActivity(intent)
        }

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 2000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2000 -> {
                val selectedImageUri: Uri? = data?.data

                if (selectedImageUri != null) {

                    if (imageUriList.size == 6) {
                        Toast.makeText(this, "이미 사진이 꽉 찼습니다.", Toast.LENGTH_SHORT).show()
                        return
                    }

                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()

                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //권한 요청 후 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()

                } else {
                    Toast.makeText(this, "권한을 거부하였습니다", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }
    }


    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle(" 권한이 필요합니다 ")
            .setMessage("전자액자에 앱에서 사진을 불러오기 위해 권한이 필요합니다")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)

            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()


    }


}