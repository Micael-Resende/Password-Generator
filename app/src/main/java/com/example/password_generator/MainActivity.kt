package com.example.password_generator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var passwordText: TextView
    private lateinit var passwordStrengthText: TextView // Adicionado o TextView de força da senha
    private lateinit var copyButton: ImageView
    private lateinit var generateButton: Button
    private lateinit var lengthSeekBar: SeekBar
    private lateinit var uppercaseSwitch: SwitchMaterial
    private lateinit var lowercaseSwitch: SwitchMaterial
    private lateinit var numbersSwitch: SwitchMaterial
    private lateinit var similarSwitch: SwitchMaterial
    private lateinit var specialCharactersSwitch: SwitchMaterial
    private lateinit var passwordLengthValue: TextView
    private lateinit var strengthView1: View
    private lateinit var strengthView2: View
    private lateinit var strengthView3: View
    private lateinit var strengthView4: View
    private var passwordLength = 8

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializando os elementos da interface
        passwordText = findViewById(R.id.passwordText)
        passwordStrengthText = findViewById(R.id.passwordStrengthText) // Inicialização do TextView de força da senha
        copyButton = findViewById(R.id.copyButton)
        generateButton = findViewById(R.id.generateButton)
        lengthSeekBar = findViewById(R.id.lengthSeekBar)
        uppercaseSwitch = findViewById(R.id.uppercaseSwitch)
        lowercaseSwitch = findViewById(R.id.lowercaseSwitch)
        numbersSwitch = findViewById(R.id.numbersSwitch)
        similarSwitch = findViewById(R.id.similarSwitch)
        specialCharactersSwitch = findViewById(R.id.specialCharactersSwitch)
        passwordLengthValue = findViewById(R.id.passwordLengthValue)
        strengthView1 = findViewById(R.id.strengthView1)
        strengthView2 = findViewById(R.id.strengthView2)
        strengthView3 = findViewById(R.id.strengthView3)
        strengthView4 = findViewById(R.id.strengthView4)

        // Configuração inicial do comprimento da senha
        passwordLengthValue.text = passwordLength.toString()
        lengthSeekBar.progress = passwordLength - 8

        // Atualiza o comprimento da senha conforme o usuário ajusta a SeekBar
        lengthSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                passwordLength = progress + 8
                passwordLengthValue.text = passwordLength.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Gera uma senha quando o botão é pressionado
        generateButton.setOnClickListener {
            generatePassword()
        }

        // Configura o clique para copiar a senha
        copyButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("password", passwordText.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
        }
    }

    // Função para calcular o nível de segurança da senha
    private fun estimateStrength(password: String): Int {
        var score = 0

        if (password.length >= 8) score++
        if (password.any { it.isUpperCase() }) score++
        if (password.any { it.isLowerCase() }) score++
        if (password.any { it.isDigit() }) score++
        if (password.any { !it.isLetterOrDigit() }) score++

        return score.coerceIn(0, 4) // Garante uma pontuação entre 0 e 4
    }

    // Atualiza os indicadores de força visualmente
    private fun updateStrengthIndicator(score: Int) {
        val activeColor = Color.GREEN
        val inactiveColor = Color.GRAY

        // Atualiza os indicadores visuais de força
        strengthView1.setBackgroundColor(if (score >= 1) activeColor else inactiveColor)
        strengthView2.setBackgroundColor(if (score >= 2) activeColor else inactiveColor)
        strengthView3.setBackgroundColor(if (score >= 3) activeColor else inactiveColor)
        strengthView4.setBackgroundColor(if (score >= 4) activeColor else inactiveColor)

        // Define o texto do nível de força dinamicamente
        passwordStrengthText.text = when (score) {
            1 -> "Weak"
            2 -> "Medium"
            3 -> "Strong"
            4 -> "Very Strong"
            else -> "Too Weak"
        }
    }

    // Função para gerar a senha
    private fun generatePassword() {
        val uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lowercase = "abcdefghijklmnopqrstuvwxyz"
        val numbers = "0123456789"
        val specialCharacters = "!@#\$%^&*()_-+=<>?/{}~|"
        val similarCharacters = "il1Lo0O"

        var characterSet = ""

        if (uppercaseSwitch.isChecked) characterSet += uppercase
        if (lowercaseSwitch.isChecked) characterSet += lowercase
        if (numbersSwitch.isChecked) characterSet += numbers
        if (specialCharactersSwitch.isChecked) characterSet += specialCharacters

        if (similarSwitch.isChecked) {
            characterSet = characterSet.filterNot { it in similarCharacters }
        }

        if (characterSet.isEmpty()) {
            Toast.makeText(this, "Select at least one option", Toast.LENGTH_SHORT).show()
            return
        }

        val password = (1..passwordLength)
            .map { characterSet[Random.nextInt(characterSet.length)] }
            .joinToString("")

        passwordText.text = password
        copyButton.visibility = View.VISIBLE // Torna o botão de copiar visível

        // Calcula e exibe a força da senha
        val strengthScore = estimateStrength(password)
        updateStrengthIndicator(strengthScore)
    }
}
