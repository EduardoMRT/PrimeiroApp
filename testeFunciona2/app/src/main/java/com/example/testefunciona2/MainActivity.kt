package com.example.testefunciona2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.testefunciona2.databinding.ActivityMainBinding
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val btnEntrar: Button = findViewById(R.id.btnEntrar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        connectToDatabase(this);

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun onClickedInCreateAccount(view: android.view.View){
        val intent = Intent(this, CriarConta::class.java);
        startActivity(intent);
    }

    //Pega os dados do Usuario e Senha
    fun onClickedInEntrar(view: android.view.View){
        val inputEditTextUsuario: EditText = findViewById(R.id.inputUsuario);
        val textUsuario = inputEditTextUsuario.text.toString();

        val inputEditTextSenha: EditText = findViewById(R.id.inputSenha);
        val textSenha = inputEditTextSenha.text.toString();



        //Mostra na tela como um alert
        Toast.makeText(this, "Você digitou: $textUsuario \n $textSenha", Toast.LENGTH_SHORT).show();
        verificaCredenciais(textUsuario, textSenha);
    }

    data class Usuarios(
        val id: Int? = null,
        val nomeUsuario: String,
        val senha: String
    )

    private fun verificaCredenciais(usuario: String, senha: String){
        val usuarios = selectUsuarios();
        for(usuarioUn in usuarios){
            println(usuarioUn.toString())
          if (usuario == usuarioUn.nomeUsuario && senha == usuarioUn.senha){
              val intent = Intent(this, MainActivity2::class.java);
              intent.putExtra("USER_NAME", usuario);
              startActivity(intent);
          }else{
              Toast.makeText(this, "Credenciais incorretas", Toast.LENGTH_LONG).show();
          }
        }
    }

    private fun selectUsuarios(): List<Usuarios> {
        val dbHelper = DatabaseHelper(this) // Inicializa o DatabaseHelper
        val db = dbHelper.openDatabase() // Abre a conexão ao banco de dados

        val usuarios = mutableListOf<Usuarios>()
        val sql = "SELECT * FROM usuarios"

        val cursor = db.rawQuery(sql, null)

        while (cursor.moveToNext()) {
            val usuario = Usuarios(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nomeUsuario = cursor.getString(cursor.getColumnIndexOrThrow("nomeUsuario")),
                senha = cursor.getString(cursor.getColumnIndexOrThrow("senha"))
            )
            usuarios.add(usuario)
        }

        cursor.close()
        dbHelper.closeDatabase() // Fechar a conexão

        return usuarios
    }




    class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "meuprimeiroapp.db";
            private const val DATABASE_VERSION = 1;
            private const val TABLE_USUARIOS = "usuarios";
        }

        override fun onCreate(db: SQLiteDatabase) {
            val createTableSQL = "CREATE TABLE $TABLE_USUARIOS (id INTEGER PRIMARY KEY AUTOINCREMENT, nome TEXT, senha TEXT)";
            db.execSQL(createTableSQL);
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS");
            onCreate(db);
        }

        fun openDatabase(): SQLiteDatabase {
            return this.writableDatabase;
        }

        fun closeDatabase() {
            this.close();
        }
    }


    private fun connectToDatabase(context: Context): SQLiteDatabase? {
        val dbHelper = DatabaseHelper(context)
        try {
            val db = dbHelper.openDatabase();
            println("Conexão estabelecida com SQLite.");
            return db;
        } catch (e: Exception) {
            println("Erro ao conectar: ${e.message}");
            return null;
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


}