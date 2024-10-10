package com.example.testefunciona2

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class CriarConta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_criar_conta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun onClickedInCreateAccount(view : android.view.View){

        val inputNome: EditText = findViewById(R.id.inputNome);
        val usuario = inputNome.text.toString();

        val inputSenha: EditText = findViewById(R.id.inputSenha);
        val senha = inputSenha.text.toString();

        val inputConfSenha: EditText = findViewById(R.id.inputConfSenha);
        val confSenha = inputSenha.text.toString();

        if (senha == confSenha) {
            val dbHelper = DatabaseHelper(this)

            try {
                dbHelper.insertUsuario(usuario, senha)
                voltarTelaInicial()
            } catch (e: Exception) {
                println("Ocorreu um erro ao tentar criar a conta: ${e.message}")
                showAlertDialog(this, "Erro", "Ocorreu um erro ao tentar criar o usuario")
            }
        } else {
            showAlertDialog(this, "Senha não confere", "As senhas digitadas não são iguais!")
        }
    }

    private fun voltarTelaInicial(){
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
    }

    private fun showAlertDialog(context: Context, titulo: String, msg: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titulo)
            .setMessage(msg)
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss();
            }
//            .setNegativeButton("Cancelar") { dialog, which ->
//                dialog.dismiss() ;
//            }
        // Criar e mostrar o AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()
    }


    class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "meuprimeiroapp.db"
            private const val DATABASE_VERSION = 1
            private const val TABLE_USUARIOS = "usuarios"
        }

        override fun onCreate(db: SQLiteDatabase) {
            val createTableSQL = "CREATE TABLE $TABLE_USUARIOS (id INTEGER PRIMARY KEY AUTOINCREMENT, nomeUsuario TEXT, senha TEXT)"
            db.execSQL(createTableSQL)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
            onCreate(db)
        }

        fun insertUsuario(nome: String, senha: String) {
            val db = this.writableDatabase // Abre o banco de dados em modo de escrita
            val contentValues = ContentValues().apply {
                put("nomeUsuario", nome) // Coluna "nomeUsuario"
                put("senha", senha) // Coluna "senha"
            }

            db.insert(TABLE_USUARIOS, null, contentValues) // Insere os valores na tabela
            db.close() // Fecha o banco de dados
        }
    }


}