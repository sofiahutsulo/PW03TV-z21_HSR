package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sqrt
import com.example.myapplication.R   // ← ВАЖНО: добавить эту строку


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editPower = findViewById<EditText>(R.id.editPower)
        val editPrice = findViewById<EditText>(R.id.editPrice)
        val editHours = findViewById<EditText>(R.id.editHours)
        val editSigma1 = findViewById<EditText>(R.id.editSigma1)
        val editSigma2 = findViewById<EditText>(R.id.editSigma2)
        val buttonCalc = findViewById<Button>(R.id.buttonCalcPr3)
        val textResult = findViewById<TextView>(R.id.textResultPr3)

        buttonCalc.setOnClickListener {
            val Pc = readDouble(editPower)
            val B = readDouble(editPrice)
            val T = readDouble(editHours)
            val sigma1 = readDouble(editSigma1)
            val sigma2 = readDouble(editSigma2)

            if (Pc == null || B == null || T == null || sigma1 == null || sigma2 == null) {
                textResult.text = "Перевірте введені дані – всі поля мають бути заповнені числами."
                return@setOnClickListener
            }

            val baseProfit = Pc * T * B

            // E|e| = σ * sqrt(2 / π)
            val k = sqrt(2.0 / PI)

            val meanAbsError1 = sigma1 * k
            val meanAbsError2 = sigma2 * k

            // втрати прибутку через балансуючу енергію:
            // L = B * E|e| * T
            val loss1 = B * meanAbsError1 * T
            val loss2 = B * meanAbsError2 * T

            val profit1 = baseProfit - loss1
            val profit2 = baseProfit - loss2
            val deltaProfit = profit2 - profit1

            val resultText = buildString {
                appendLine("Вихідні дані:")
                appendLine("Pc = ${fmt(Pc)} МВт; B = ${fmt(B)} грн/МВт·год; T = ${fmt(T)} год;")
                appendLine("σ₁ = ${fmt(sigma1)} МВт; σ₂ = ${fmt(sigma2)} МВт;")
                appendLine()

                appendLine("1) Базовий прибуток без похибки прогнозу:")
                appendLine("P₀ = Pc · T · B = ${fmt(baseProfit)} грн")
                appendLine()

                appendLine("2) Середній модуль похибки прогнозу (E|e| = σ·√(2/π)):")
                appendLine("E|e₁| = ${fmt(meanAbsError1)} МВт")
                appendLine("E|e₂| = ${fmt(meanAbsError2)} МВт")
                appendLine()

                appendLine("3) Втрати прибутку через балансуючу енергію:")
                appendLine("L₁ = B · E|e₁| · T = ${fmt(loss1)} грн")
                appendLine("L₂ = B · E|e₂| · T = ${fmt(loss2)} грн")
                appendLine()

                appendLine("4) Очікуваний прибуток СЕС:")
                appendLine("P₁ = P₀ - L₁ = ${fmt(profit1)} грн (до покращення прогнозу)")
                appendLine("P₂ = P₀ - L₂ = ${fmt(profit2)} грн (після покращення прогнозу)")
                appendLine()

                appendLine("5) Приріст прибутку за рахунок покращення прогнозу:")
                appendLine("ΔP = P₂ - P₁ = ${fmt(deltaProfit)} грн")
            }

            textResult.text = resultText
        }
    }

    private fun readDouble(editText: EditText): Double? {
        val s = editText.text.toString()
            .replace(",", ".")
            .trim()
        return s.toDoubleOrNull()
    }

    private fun fmt(x: Double, digits: Int = 2): String {
        return String.format(Locale.US, "%.${digits}f", x)
    }
}
