package com.example.contadormg

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GraficaView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var datos: List<Pair<String, Int>> = emptyList()

    private val pincelBarra = Paint().apply {
        color = Color.parseColor("#6650a4")
        isAntiAlias = true
    }

    private val pincelTexto = Paint().apply {
        color = Color.GRAY
        textSize = 28f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    fun setDatos(nuevosDatos: List<Pair<String, Int>>) {
        datos = nuevosDatos
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (datos.isEmpty()) {
            canvas.drawText("Sin series aún", width / 2f, height / 2f, pincelTexto)
            return
        }

        val maximo = datos.maxOf { it.second }.coerceAtLeast(1)
        val anchoBarra = width.toFloat() / datos.size
        val alturaDisponible = height - 60f

        datos.forEachIndexed { indice, (etiqueta, valor) ->
            val alturaBarra = (valor.toFloat() / maximo) * alturaDisponible
            val izquierda = indice * anchoBarra + anchoBarra * 0.15f
            val derecha = (indice + 1) * anchoBarra - anchoBarra * 0.15f
            val arriba = alturaDisponible - alturaBarra

            canvas.drawRect(izquierda, arriba, derecha, alturaDisponible, pincelBarra)

            val centroX = (izquierda + derecha) / 2
            canvas.drawText(valor.toString(), centroX, arriba - 8f, pincelTexto)
            canvas.drawText(etiqueta, centroX, height - 10f, pincelTexto)
        }
    }
}