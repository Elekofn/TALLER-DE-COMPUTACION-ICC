package com.example.contadormg

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var contador = 0

        val tvRepeticiones = findViewById<TextView>(R.id.tvRepeticiones)
        val btnSumar = findViewById<Button>(R.id.btnSumar)
        val btnRestar = findViewById<Button>(R.id.btnRestar)
        val btnReiniciar = findViewById<Button>(R.id.btnReiniciar)

        btnSumar.setOnClickListener {
            contador++
            tvRepeticiones.text = contador.toString()
        }

        btnRestar.setOnClickListener {
            if (contador > 0) {
                contador--
                tvRepeticiones.text = contador.toString()
            }
        }

        btnReiniciar.setOnClickListener {
            contador = 0
            tvRepeticiones.text = contador.toString()
        }
    }
}