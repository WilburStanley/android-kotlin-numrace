package com.techvalentine.numrace

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.text.InputFilter
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private lateinit var targetNumber: EditText
    private lateinit var player1Txt: EditText
    private lateinit var player2Txt: EditText
    private lateinit var inputtedNumTxt: EditText
    private lateinit var playGameBtn: AppCompatButton
    private lateinit var resetGameBtn: AppCompatButton
    private lateinit var gameConfigMainContainer: LinearLayout
    private lateinit var gameContainer: LinearLayout
    private lateinit var titleBar: TextView
    private lateinit var playerToMove: TextView
    private lateinit var currentNumValue: TextView
    private lateinit var player1Title: TextView
    private lateinit var player2Title: TextView
    private lateinit var addBtn: AppCompatImageButton
    private lateinit var goBackBtn: AppCompatImageButton

    // Variable to track the current player
    private var isPlayer1Turn = true

    // Variables to store player names
    private lateinit var player1: String
    private lateinit var player2: String
    private lateinit var player1ListView: ListView
    private lateinit var player2ListView: ListView
    private lateinit var player1Adapter: ArrayAdapter<String>
    private lateinit var player2Adapter: ArrayAdapter<String>

    private val player1Values = mutableListOf<Int>()
    private val player2Values = mutableListOf<Int>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initialization()

        playGameBtn.setOnClickListener {
            player1 = player1Txt.text.toString().trim()
            player2 = player2Txt.text.toString().trim()

            // Check for empty or duplicate names
            if (player1.isEmpty() || player2.isEmpty()) {
                Toast.makeText(this, "Complete the input fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (player1.equals(player2, ignoreCase = true)) {
                Toast.makeText(this, "Players must have different names", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Continue if names are valid
            player1Title.text = player1
            player2Title.text = player2

            val startingPlayer = if (Random.nextBoolean()) player1 else player2
            playerToMove.text = "Starting Player: $startingPlayer"
            isPlayer1Turn = startingPlayer == player1

            val targetNumValEdittext = targetNumber.text.toString().trim()
            val targetNumVal = targetNumValEdittext.toIntOrNull()

            if (targetNumVal in 40..65) {
                titleBar.text = "Target: $targetNumVal"
                startGame()
            } else {
                Toast.makeText(this, "40 - 65 ONLY", Toast.LENGTH_SHORT).show()
                targetNumber.text.clear()
            }
        }

        goBackBtn.setOnClickListener {
            resetGame()
        }
        resetGameBtn.setOnClickListener {
            resetGame()
        }
    }
    @SuppressLint("ServiceCast")
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            currentFocus?.let { view ->
                if (view is EditText) {
                    val rect = Rect()
                    view.getGlobalVisibleRect(rect)
                    if (!rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                        view.clearFocus()
                        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
    private fun initialization() {
        // TextView
        titleBar = findViewById(R.id.titleBar)
        playerToMove = findViewById(R.id.playerToMoveTxt)
        currentNumValue = findViewById(R.id.currentNumValue)
        // EditText
        targetNumber = findViewById(R.id.targetNumberTxt)
        player1Txt = findViewById(R.id.player1Txt)
        player2Txt = findViewById(R.id.player2Txt)
        inputtedNumTxt = findViewById(R.id.inputtedNumTxt)
        // AppCompatButton
        playGameBtn = findViewById(R.id.playGameBtn)
        resetGameBtn = findViewById(R.id.resetGameBtn)
        // LinearLayout
        gameConfigMainContainer = findViewById(R.id.gameConfigMainContainer)
        gameContainer = findViewById(R.id.gameContainer)
        // AppCompatImageButton
        addBtn = findViewById(R.id.addBtn)

        val inputFilter = InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("[1-9]"))) {
                source // Allow the input if it's a single digit between 1-9
            } else {
                "" // Block the input if it's not valid
            }
        }
        // Combine input filter to restrict input length to 1 character
        inputtedNumTxt.filters = arrayOf(InputFilter.LengthFilter(1), inputFilter)

        goBackBtn = findViewById(R.id.goBackBtn)

        player1ListView = findViewById(R.id.player1ListView)
        player2ListView = findViewById(R.id.player2ListView)

        val itemLayout = R.layout.list_item
        // Create ArrayAdapters for the ListViews
        player1Adapter = ArrayAdapter(this, itemLayout, R.id.list_item_text, mutableListOf<String>())
        player2Adapter = ArrayAdapter(this, itemLayout, R.id.list_item_text, mutableListOf<String>())

        // Set the adapters to the ListViews
        player1ListView.adapter = player1Adapter
        player2ListView.adapter = player2Adapter

        player1Title = findViewById(R.id.player1Title)
        player2Title = findViewById(R.id.player2Title)
    }
    @SuppressLint("SetTextI18n")
    private fun resetGame() {
        gameConfigMainContainer.visibility = View.VISIBLE
        gameContainer.visibility = View.GONE
        goBackBtn.visibility = View.GONE
        resetGameBtn.visibility = View.GONE

        titleBar.text = getString(R.string.race_to_a_number)

        player1Txt.text.clear()
        player2Txt.text.clear()
        targetNumber.text.clear()
        inputtedNumTxt.text.clear()
        currentNumValue.text = "0"

        isPlayer1Turn = true
        playerToMove.text = "Player to move: ${player1Txt.text.toString().trim()}"

        addBtn.isEnabled = true
        inputtedNumTxt.isEnabled = true

        // Clear the lists
        player1Values.clear()
        player2Values.clear()
        // Notify adapters of the changes

        player1Adapter.clear()
        player2Adapter.clear()

        // Notify adapters to refresh the ListViews
        player1Adapter.notifyDataSetChanged()
        player2Adapter.notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    private fun startGame() {
        gameConfigMainContainer.visibility = View.GONE
        gameContainer.visibility = View.VISIBLE
        goBackBtn.visibility = View.VISIBLE

        addBtn.setOnClickListener {
            val currentValue = currentNumValue.text.toString().toIntOrNull() ?: 0
            val inputtedNumValue = inputtedNumTxt.text.toString().toIntOrNull()

            if (inputtedNumValue != null) {
                val newValue = currentValue + inputtedNumValue
                currentNumValue.text = newValue.toString()
                // Add value to the current player's list
                if (isPlayer1Turn) {
                    player1Values.add(inputtedNumValue)
                    player1Adapter.add(inputtedNumValue.toString())
                } else {
                    player2Values.add(inputtedNumValue)
                    player2Adapter.add(inputtedNumValue.toString())
                }
                // Notify adapters of the change
                player1Adapter.notifyDataSetChanged()
                player2Adapter.notifyDataSetChanged()

                val targetNum = targetNumber.text.toString().toIntOrNull()

                if (targetNum != null) {
                    if (newValue >= targetNum) {
                        playerToMove.text = "Winner: ${if (isPlayer1Turn) player1 else player2}"
                        addBtn.isEnabled = false
                        inputtedNumTxt.isEnabled = false
                        resetGameBtn.visibility = View.VISIBLE
                    } else {
                        // Switch player turn
                        isPlayer1Turn = !isPlayer1Turn
                        playerToMove.text = if (isPlayer1Turn) {
                            "Player to move: $player1"
                        } else {
                            "Player to move: $player2"
                        }
                        inputtedNumTxt.text.clear()
                    }
                } else {
                    Toast.makeText(this, "Invalid target number", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}