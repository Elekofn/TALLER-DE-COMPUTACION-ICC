package com.example.contadormg

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private var contador = 0
    private lateinit var tvRepeticiones: TextView
    private lateinit var tvHoy: TextView
    private lateinit var tvTituloGrafica: TextView
    private lateinit var etEjercicio: EditText
    private lateinit var grafica: GraficaView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        tvRepeticiones = findViewById(R.id.tvRepeticiones)
        tvHoy = findViewById(R.id.tvHoy)
        tvTituloGrafica = findViewById(R.id.tvTituloGrafica)
        etEjercicio = findViewById(R.id.etEjercicio)
        grafica = findViewById(R.id.grafica)
        val btnGuardar = findViewById<Button>(R.id.btnGuardar)
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

        btnGuardar.setOnClickListener {
            val ejercicio = nombreEjercicio()

            if (ejercicio.isEmpty()) {
                Toast.makeText(this, "Escribe el nombre del ejercicio", Toast.LENGTH_SHORT).show()
            } else if (contador <= 0) {
                Toast.makeText(this, "No hay repeticiones que guardar", Toast.LENGTH_SHORT).show()
            } else {
                val datos = leerProgreso()
                val hoy = fechaHoy()

                val ejerciciosDelDia = datos.optJSONObject(hoy) ?: JSONObject()
                val series = ejerciciosDelDia.optJSONArray(ejercicio) ?: JSONArray()

                series.put(contador)
                ejerciciosDelDia.put(ejercicio, series)
                datos.put(hoy, ejerciciosDelDia)

                guardarProgreso(datos)

                contador = 0
                tvRepeticiones.text = "0"
                actualizarPantalla()

                Toast.makeText(this, "Serie guardada en $ejercicio", Toast.LENGTH_SHORT).show()
            }
        }

        etEjercicio.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                actualizarPantalla()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        actualizarPantalla()
    }

    private fun nombreEjercicio(): String {
        return etEjercicio.text.toString().trim()
    }

    private fun fechaHoy(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formato.format(Date())
    }

    private fun leerProgreso(): JSONObject {
        val archivo = File(filesDir, "progreso.json")
        return if (archivo.exists()) {
            try {
                JSONObject(archivo.readText())
            } catch (e: Exception) {
                JSONObject()
            }
        } else {
            JSONObject()
        }
    }

    private fun guardarProgreso(datos: JSONObject) {
        val archivo = File(filesDir, "progreso.json")
        archivo.writeText(datos.toString())
    }

    private fun seriesDelEjercicio(): JSONArray {
        val ejercicio = nombreEjercicio()
        if (ejercicio.isEmpty()) return JSONArray()

        val ejerciciosDelDia = leerProgreso().optJSONObject(fechaHoy()) ?: return JSONArray()
        return ejerciciosDelDia.optJSONArray(ejercicio) ?: JSONArray()
    }

    private fun actualizarPantalla() {
        val ejercicio = nombreEjercicio()
        val series = seriesDelEjercicio()

        var total = 0
        val lista = mutableListOf<Pair<String, Int>>()

        for (i in 0 until series.length()) {
            val reps = series.optInt(i, 0)
            total += reps
            lista.add(Pair("S${i + 1}", reps))
        }

        if (ejercicio.isEmpty()) {
            tvHoy.text = "Escribe un ejercicio para ver su progreso"
            tvTituloGrafica.text = "Series de hoy"
        } else {
            tvHoy.text = "$ejercicio hoy: $total reps en ${series.length()} series"
            tvTituloGrafica.text = "Series de $ejercicio"
        }

        grafica.setDatos(lista)
    }
}