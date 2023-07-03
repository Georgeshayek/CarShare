package com.example.project

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileSet:AppCompatActivity() {
    private lateinit var Picture: ImageView
    private var filepath: String? = null
    private lateinit var Name: TextView
    private lateinit var email: TextView
    private lateinit var password: TextView
    private lateinit var editprof: Button
    private lateinit var changePhoto: Button
    private lateinit var logout: Button
    private lateinit var history: Button
    private val pickImage = 100

    private var imageUri: Uri? = null

    private val REQUEST_IMAGE_CAPTURE = 1

    private val REQUEST_CODE_SELECT_IMAGE = 1001
    private val REQUEST_CODE_READ_EXTERNAL_STORAGE = 1002
    @SuppressLint("MissingInflatedId", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        // val SignupTextview = findViewById<TextView>(R.id.textView6)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profileset_layout)
        val toolbar = findViewById<RelativeLayout>(R.id.toolbar)
        toolbar.findViewById<ImageButton>(R.id.back_button)?.setOnClickListener {
            finish()
        }
        var dbhelp=DB_helper(this)
        var db=dbhelp.readableDatabase

        Picture=findViewById(R.id.profile_image)
        Name =findViewById<EditText>(R.id.name)
        email =findViewById<EditText>(R.id.email)
        password=findViewById<EditText>(R.id.pswd)
        editprof=findViewById(R.id.editbutton)
        changePhoto = findViewById(R.id.editphoto)

        val message = SharedPreferencesUtils.getEmail(this)
        val query="SELECT * FROM user WHERE email='"+message+"'"
        val rs=db.rawQuery(query,null)
        rs.moveToFirst()
        Name.text=(rs.getString(rs.getColumnIndex("name")) )
        password.text=(rs.getInt(rs.getColumnIndex("phone")).toString() )
        val imagesrc=rs.getString(rs.getColumnIndex("img"))

        email.text=(message)
        if(!imagesrc.isNullOrEmpty()){
            val bitmap = BitmapFactory.decodeFile(imagesrc)
            Picture.setImageBitmap(bitmap)
        }

        editprof.setOnClickListener {


                val intent = Intent (this, EditProfileActivity::class.java)
                startActivity(intent)

        }





        changePhoto.setOnClickListener {

            val popupMenu = PopupMenu(this, changePhoto)


            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)


            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem.itemId) {
                    R.id.action_item_1 -> {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                                REQUEST_CODE_READ_EXTERNAL_STORAGE)
                        }


                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
                        true
                    }
                    R.id.action_item_2 -> {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

                        true
                    }
                    else -> false
                }
            }


            popupMenu.show()
        }

    }
    @SuppressLint("Range", "SuspiciousIndentation")
    override fun onResume() {
        super.onResume()
        var dbhelp=DB_helper(this)
        var db=dbhelp.readableDatabase




        val message = SharedPreferencesUtils.getEmail(this)
        val query="SELECT * FROM user WHERE email='"+message+"'"
        val rs=db.rawQuery(query,null)
        rs.moveToFirst()
        Name.text=(rs.getString(rs.getColumnIndex("name")) )
        password.text=(rs.getInt(rs.getColumnIndex("phone")).toString() )
        email.text=(message)
        val imagesrc=rs.getString(rs.getColumnIndex("img"))

        if(!imagesrc.isNullOrEmpty()){
            val bitmap = BitmapFactory.decodeFile(imagesrc)
            Picture.setImageBitmap(bitmap)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK)  {

            val imageUri: Uri = data?.data ?: return // Get the Uri of the selected image
            filepath = getRealPathFromUri(this, imageUri)
            val message = SharedPreferencesUtils.getEmail(this)

            var data1 = ContentValues(

            )
            var dbhelp=DB_helper(this)
            var db=dbhelp.readableDatabase
            data1.put("img", filepath.toString())
            val where = "email=?"
            val whereArgs = arrayOf<String>(java.lang.String.valueOf(message))
            try {
                db.update("user", data1, where, whereArgs)
                val bitmap = BitmapFactory.decodeFile(filepath)
                Picture.setImageBitmap(bitmap)
                Toast.makeText(this,"succesfully updated!", Toast.LENGTH_SHORT).show()


            } catch (e: Exception) {
                val error = e.message.toString()
                Toast.makeText(this,error, Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            val imagePath = saveImageToInternalStorage(imageBitmap)
            saveImagePathToDatabase(imagePath)
        }

    }
    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val wrapper = ContextWrapper(this.applicationContext)
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }
    private fun saveImagePathToDatabase(imagePath: String) {
        val data1 = ContentValues().apply {
            put("img", imagePath)
        }
        val message = SharedPreferencesUtils.getEmail(this)
        val db = DB_helper(this).writableDatabase
        val where = "email=?"
        val whereArgs = arrayOf<String>(java.lang.String.valueOf(message))
        try {
            db.update("user", data1, where, whereArgs)
            val bitmap = BitmapFactory.decodeFile(filepath)
            Picture.setImageBitmap(bitmap)
            Toast.makeText(this,"succesfully updated up!", Toast.LENGTH_SHORT).show()


        } catch (e: Exception) {
            val error = e.message.toString()
            Toast.makeText(this,error, Toast.LENGTH_SHORT).show()
        }
    }
    private fun getRealPathFromUri(context: Context, uri: Uri): String {
        var filePath = ""
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
                filePath = it.getString(columnIndex)
            }
            it.close()
        }
        return filePath
    }
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? =this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            filepath = absolutePath
        }
    }



}
