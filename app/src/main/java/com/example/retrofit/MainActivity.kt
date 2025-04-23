package com.example.retrofit

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Model
data class User(
    val id: Int = 0,
    val name: String,
    val email: String
)

// Retrofit API
interface ApiService {
    @GET("api/users")
    fun getUsers(): Call<List<User>>

    @POST("api/users")
    fun addUser(@Body user: User): Call<Void>
}

// Retrofit Singleton
object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://mobile.up.railway.app/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// Activity
class MainActivity : AppCompatActivity() {
    lateinit var editName: EditText
    lateinit var editEmail: EditText
    lateinit var btnAdd: Button
    lateinit var txtOutput: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editName = findViewById(R.id.editName)
        editEmail = findViewById(R.id.editEmail)
        btnAdd = findViewById(R.id.btnAdd)
        txtOutput = findViewById(R.id.txtOutput)

        getUsers()

        btnAdd.setOnClickListener {
            val name = editName.text.toString()
            val email = editEmail.text.toString()
            addUser(User(name = name, email = email))
        }
    }

    private fun getUsers() {
        RetrofitInstance.api.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                val users = response.body()
                txtOutput.text = users?.joinToString("\n") { "${it.id}. ${it.name}, ${it.email}" } ?: "Kosong"
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                txtOutput.text = "Error: ${t.message}"
            }
        })
    }

    private fun addUser(user: User) {
        RetrofitInstance.api.addUser(user).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(this@MainActivity, "User ditambahkan", Toast.LENGTH_SHORT).show()
                getUsers()
                editName.text.clear()
                editEmail.text.clear()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal tambah user", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
