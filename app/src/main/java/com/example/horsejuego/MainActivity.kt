package com.example.horsejuego

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.icu.text.SimpleDateFormat
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.test.runner.screenshot.ScreenCapture
import androidx.test.runner.screenshot.Screenshot.capture
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.logging.SimpleFormatter

class MainActivity : AppCompatActivity() {



    private  var bitmap : Bitmap? = null
    private var mHandles : Handler? = null
    private var timeInSeconds : Long = 0
    private  var widht_bonus = 0
    private var gaming = true
    private var string_share = ""

    private var cellSelected_x = 0
    private var celSelected_y = 0
    private var lives = 1
    private var scoreLevel = 1
    private var score_lives = 1

    private var nextLevel = false


    private  var level = 0
    private var levelMoves = 0

    private  var movesRequired = 0
    private  var moves = 0
    private var options = 0
    private  var bonus = 0

    private  var  checkMovement = true

    private var nameColorBlack = "black_cell"
    private var nameColorWhite = "white_cell"

    private lateinit var boar: Array<IntArray>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initScreenGame()
        starGame()
    }

    fun chechChelClickend(v: View) {

        var name = v.tag.toString()
        var x = name.subSequence(1, 2).toString().toInt()
        var y = name.subSequence(2, 3).toString().toInt()

        checkCell(x, y)


    }
    private fun checkCell(x: Int, y: Int) {


        var checTrue = true
        if (checkMovement) {

            var dif_x = x - cellSelected_x
            var dif_y = y - celSelected_y

            checTrue = false

            if (dif_x == 1 && dif_y == 2) checTrue = true
            if (dif_x == 1 && dif_y == -2) checTrue = true
            if (dif_x == 2 && dif_y == 1) checTrue = true
            if (dif_x == 2 && dif_y == -1) checTrue = true
            if (dif_x == -1 && dif_y == 2) checTrue = true
            if (dif_x == -1 && dif_y == -2) checTrue = true
            if (dif_x == -2 && dif_y == 1) checTrue = true
            if (dif_x == -2 && dif_y == -1) checTrue = true
        }
        else{
            if (boar [x][y]  != 1)
                bonus--
            var tvBonusData = findViewById<TextView>(R.id.tvBonusData)
            tvBonusData.text = " + $bonus"

            if (bonus == 0) tvBonusData.text = " "
        }
        if (boar[x][y] == 1) checTrue = false

        if (checTrue) selectCell(x, y)
    }

    private fun resetBoard() {
        boar = arrayOf(
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
            intArrayOf(0, 0, 0, 0, 0, 0, 0, 0),
        )
    }
    private fun clearBoar(){
        var iv : ImageView

        var colorBlack = ContextCompat.getColor(this, resources.getIdentifier(nameColorBlack, "color", packageName))
        var colorWhite = ContextCompat.getColor(this, resources.getIdentifier(nameColorWhite, "color", packageName))

        for (i in 0..7){
            for (j in 0..7){

                iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))
                //  iv.setImageResource(R.drawable.caballo)
                iv.setImageResource(0)

                if (checkColorCell(i, j) == "black") iv.setBackgroundColor(colorBlack)
                else  iv.setBackgroundColor(colorWhite)
            }
        }
    }
    private fun setFirstPossition() {
        var x = 0
        var y = 0


        var firstPosition = false

        while (firstPosition == false){


            x = (0..7).random()
            y = (0..7).random()
            if (boar[x][y] == 0){ firstPosition = true
                chechOption(x, y)}
            if (options == 0) firstPosition = false
        }


        cellSelected_x = x
        celSelected_y = y

        selectCell(x, y)


    }
    private fun  setLevel(){
        if (nextLevel){
            level++
            lives++
        }else{
            lives--
            if (lives < 1){
                level = 1
                lives = 1

            }
        }

    }
    private fun setLevelParameters(){
        var tvLiveData =findViewById<TextView>(R.id.tvLiveData)
        tvLiveData.text = lives.toString()

        score_lives = lives

        var tvLevelNumber = findViewById<TextView>(R.id.tvLevelNumber)
        tvLevelNumber.text = level.toString()
        scoreLevel = level

        bonus = 0
        var tvBonusData = findViewById<TextView>(R.id.tvBonusData)
        tvBonusData.text = ""

        setLevelMoves()
        moves = levelMoves

        movesRequired = setMovesRequired()


    }
    private fun setMovesRequired():Int{

        var movesRequired = 0
        when (level) {
            1 -> movesRequired = 4
            2 -> movesRequired = 4
            3 -> movesRequired = 4
            4 -> movesRequired = 5
            5 -> movesRequired = 5
            6 -> movesRequired = 5
            7 -> movesRequired = 6
            8 -> movesRequired = 6
            9 -> movesRequired = 6
            10 -> movesRequired = 7
            11 -> movesRequired = 7
            12 -> movesRequired = 7
            13 -> movesRequired = 8

        }

        return movesRequired
    }
    private  fun setLevelMoves(){

        when(level){

            1-> levelMoves = 64
            2-> levelMoves = 56
            3-> levelMoves = 31
            4-> levelMoves = 16
            5-> levelMoves = 25
            6-> levelMoves = 36
            7-> levelMoves = 30
            8-> levelMoves = 25
            9-> levelMoves = 22
            10-> levelMoves = 17
            11-> levelMoves = 20
            12-> levelMoves = 23
            13-> levelMoves = 21


        }
    }
    private  fun setBoardLevel(){
        when(level){

            2-> paintLevel_2()
            3-> paintLevel_3()
            4-> paintLevel_4()
            5-> paintLevel_5()
            6-> paintLevel_6()
            7-> paintLevel_7()
            8-> paintLevel_8()
            9-> paintLevel_9()
            10-> paintLevel_10()
            11-> paintLevel_11()
            12-> paintLevel_12()
            13-> paintLevel_13()
        }
    }
    private fun paint_Column( colunm: Int) {
        for (i in 0..7) {
            boar[colunm][i] = 1
            painHorseCell(colunm, i, "previus_cell")
        }
    }
    private fun paint_Row(row: Int){
        for (i in 0..7) {
            boar[i][row] = 1
            painHorseCell(i, row, "previus_cell")
        }

    }
    private fun paintLevel_2(){
        paint_Column(6)
    }
    private fun paintLevel_3(){
        for (i in 0..7){
            for (j in 4..7){
                boar[j][i] = 1
                painHorseCell(j, i, "previus_cell")
            }
        }
    }
    private fun paintLevel_4(){
        for (i in 0..3){
            for (j in 5..7){
                boar[j][i] = 1
                painHorseCell(j, i, "previus_cell")
            }
        }
    }
    private fun paintLevel_5(){
        for (i in 0..3){
            for (j in 0..3){
                painHorseCell(j, i, "previus_cell")
            }
        }
    }
    private fun paintLevel_6(){
        paint_Column(4)
        paint_Row(7)
    }
    private fun paintLevel_7(){

        paintLevel_2();  paintLevel_6()

    }
    private fun paintLevel_8(){
        for (i in 0..2){
            for (j in 5..7){
                painHorseCell(j, i, "previus_cell")
            }
        }
    }
    private fun paintLevel_9(){
        paint_Column(0)
        paint_Column(7)
        paint_Row(1)
        paint_Row(6)
    }
    private fun paintLevel_10(){
        paint_Row(5)
        paint_Row(4)
    }
    private fun paintLevel_11(){
        paint_Column(5)
        paint_Column(4)
    }
    private fun paintLevel_12(){
        paintLevel_10(); paintLevel_11()
    }
    private fun paintLevel_13(){
        for (i in 5..7){
            for (j in 0..2){
                painHorseCell(j, i, "previus_cell")
            }
        }
    }
    private  fun growProgresBonus(){

        var moves_done = levelMoves - moves
        var bonus_done = moves_done / movesRequired
        var moves_res = movesRequired * (bonus_done)
        var bonus_grow = moves_done - moves_res

        var v = findViewById<View>(R.id.vNewBonus)
        var widthBonus = ((widht_bonus/movesRequired) * bonus_grow).toFloat()

        var heigth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, getResources().getDisplayMetrics()).toInt()
        var widht = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthBonus, getResources().getDisplayMetrics()).toInt()
        v.setLayoutParams(TableRow.LayoutParams(widht, heigth))
    }
    private fun selectCell(x: Int, y: Int) {

        moves--
        var tvMovesData = findViewById<TextView>(R.id.TvMovesData)
        tvMovesData.text = moves.toString()

        growProgresBonus()

        if (boar[x][y] == 2) {
            bonus++
            var tvBonusDta = findViewById<TextView>(R.id.tvBonusData)
            tvBonusDta.text = " + $bonus"
        }



        boar[x][y] = 1
        painHorseCell(cellSelected_x, celSelected_y, "previus_cell")

        cellSelected_x = x
        celSelected_y = y


        clearOptions()
        painHorseCell(x, y, "selected_cell")
        checkMovement = true
        chechOption(x, y)

        if (moves > 0){
            checkNewBonus()
            checkGameOver()

        }
        else showMessage("You Win!!", "Nex Level",false )

    }
    private fun checkGameOver(){
        if (options == 0){

            if(bonus > 0){

                checkMovement = false
                paintAllOption()
            }
            else{
                showMessage("Game Over", "Try again!",true )

            }
        }

    }
    private  fun paintAllOption(){
        for (i in 0..7){
            for (j in 0..7){
                if(boar[i][j]  != 1)  painOptions(i, j)
                if (boar[i][j] == 0) boar[i][j]  = 9
            }
        }
    }
    private fun  showMessage (title: String, action: String, gameOver : Boolean){

        gaming = false
        nextLevel = !gameOver
        var lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.VISIBLE

        var tvTitleMessage = findViewById<TextView>(R.id.tvTitleMessage)
        tvTitleMessage.text = title

        var tvTimeData = findViewById<TextView>(R.id.tvTimesData)
        var score : String = ""
        if (gameOver){

            score = "Score: " + (levelMoves -moves) + " / " + levelMoves
            string_share = " level not completed "+ score+" "

        }
        else{
            score = tvTimeData.text.toString()
            string_share = "New challenge completed. level: $level (" + score+")"

        }
        var tvScoreMessage  = findViewById<TextView>(R.id.tvScoreMessage)
        tvScoreMessage.text = score

        var tvAction = findViewById<TextView>(R.id.tvAction)
        tvAction.text = action

    }
    private fun checkNewBonus() {

        if (moves % movesRequired == 0) {
            var bonusCell_x = 0
            var bonusCell_y = 0

            var bonusCell = false

            while (bonusCell == false) {
                bonusCell_x = (0..7).random()
                bonusCell_y = (0..7).random()
                if (boar[bonusCell_x][bonusCell_y] == 0) bonusCell = true
            }
            boar[bonusCell_x][bonusCell_y] = 2
            painBonusCell(bonusCell_x, bonusCell_y)
        }
    }
    private fun painBonusCell(x: Int, y: Int) {
        var iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        iv.setImageResource(R.drawable.bonus)
    }
    private  fun clearOption(x : Int, y : Int){

        var iv: ImageView = findViewById(resources.getIdentifier("c$x$y","id", packageName))

        if (checkColorCell(x, y) == "black")
            iv.setBackgroundColor(ContextCompat.getColor(this,resources.getIdentifier(nameColorBlack,"color",  packageName)))
        else
            iv.setBackgroundColor(ContextCompat.getColor(this,resources.getIdentifier(nameColorWhite,"color", packageName)))


        if (boar[x][y] == 1) iv.setBackgroundColor(ContextCompat.getColor(this,resources.getIdentifier("previus_cell","color", packageName)))

    }
    private   fun clearOptions() {
        for (i in 0..7){
            for (j in 0..7) {
                if (boar[i][j] == 9 || boar[i][j] == 2){
                    if (boar[i][j] == 9) {
                        boar[i][j] = 0
                        clearOption(i, j)
                    }
                }

            }
        }
    }
    private fun chechOption(x: Int, y: Int) {

        options = 0

        checkMove(x, y, 1, 2)
        checkMove(x, y, 2, 1)
        checkMove(x, y, 1, -2)
        checkMove(x, y, 2, -1)
        checkMove(x, y, -1, 2)
        checkMove(x, y, -2, 1)
        checkMove(x, y, -1, -2)
        checkMove(x, y, -2, -1)

        var tvOptionData = findViewById<TextView>(R.id.tvOptionData)
        tvOptionData.text = options.toString()
    }
    private fun checkMove(x: Int, y: Int, mov_x: Int, mov_y: Int) {

        var option_x = x + mov_x
        var option_y = y + mov_y

        if (option_x < 8 && option_y < 8 && option_x >= 0 && option_y >= 0) {
            if (boar[option_x][option_y] == 0 || boar[option_x][option_y] == 2) {
                options++
                painOptions(option_x, option_y)


                if (boar[option_x][option_y] == 0) boar[option_x][option_y] = 9
            }


        }
    }
    private fun checkColorCell(x: Int, y: Int): String {

        var color = ""
        var blackColum_x = arrayOf(0, 2, 4, 6)
        var blackRow_x = arrayOf(1, 3, 5, 7)
        if (blackColum_x.contains(x) && blackColum_x.contains(y) || blackRow_x.contains(x) && blackRow_x.contains(y))
            color = "black"
        else color = "white"

        return color

    }
    private fun painOptions(x: Int, y: Int) {
        var iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        if (checkColorCell(x, y) == "black") iv.setBackgroundResource(R.drawable.option_black)
        else iv.setBackgroundResource(R.drawable.option_white)
    }
    private fun painHorseCell(x: Int, y: Int, color: String) {
        var iv: ImageView = findViewById(resources.getIdentifier("c$x$y", "id", packageName))
        iv.setBackgroundColor(
            ContextCompat.getColor(
                this,
                resources.getIdentifier(color, "color", packageName)
            )
        )
        iv.setImageResource(R.drawable.caballo)
    }
    private fun initScreenGame() {
        setSizeBoard()
        hidemessage(false)
    }
    private fun setSizeBoard() {
        var iv: ImageView

        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x

        var width_dp = (width / getResources().getDisplayMetrics().density)
        var lateralMarginsDP = 0
        val widht_cell = (width_dp - lateralMarginsDP) / 8
        val heigth_cell = widht_cell

        widht_bonus =  2 * widht_cell.toInt()

        for (i in 0..7) {
            for (j in 0..7) {
                iv = findViewById(resources.getIdentifier("c$i$j", "id", packageName))

                var height = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    heigth_cell,
                    getResources().getDisplayMetrics()
                ).toInt()
                var width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    widht_cell,
                    getResources().getDisplayMetrics()
                ).toInt()

                iv.setLayoutParams(TableRow.LayoutParams(width, height))
            }

        }

    }
    private fun hidemessage(start : Boolean) {
        var lyMessage = findViewById<LinearLayout>(R.id.lyMessage)
        lyMessage.visibility = View.INVISIBLE

        if (start)  starGame()

    }

    fun launchAction(v: View){
        hidemessage(true)
    }


    fun launchShareGame(v: View){
        shareGame()
    }
    private  fun shareGame(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),1)
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)

        var ssc: ScreenCapture = capture(this)
        bitmap = ssc.getBitmap()

        if (bitmap != null){
             var idGame = SimpleDateFormat("yyyy/mm/dd").format(Date())
            idGame = idGame.replace(":","")
            idGame = idGame.replace("/","")

            val path = saveImage(bitmap, "${idGame}.jpg")
            val bnpUri = Uri.parse(path)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            shareIntent.putExtra(Intent.EXTRA_STREAM, bnpUri)
            shareIntent.putExtra(Intent.EXTRA_TEXT, string_share)
            shareIntent.type  = "image/png"

            val finalShareIntent = Intent.createChooser(shareIntent, "select the app you to share the game to")
            finalShareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.startActivity(finalShareIntent)

        }


    }
    private fun saveImage (bitmap: Bitmap?, filname: String): String?{
        if (bitmap == null)
            return null
        if (android.os.Build.VERSION.SDK_INT >=  android.os.Build.VERSION_CODES.Q){
            val contenValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filname)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES + "/Screenshots")
            }
            val uri = this.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contenValues)
            if (uri != null){
                this.contentResolver.openOutputStream(uri).use {
                    if ( it == null)
                        return@use

                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, it)
                    it.flush()
                    it.close()

                    MediaScannerConnection.scanFile(this, arrayOf(uri.toString()), null, null)
                }
            }
            return uri.toString()
        }

                   val filePath = Environment.getExternalStoragePublicDirectory(
                      Environment.DIRECTORY_PICTURES + "/Screenshots"
                      ).absolutePath

                       val dir = File(filePath)
                       if (!dir.exists()) dir.mkdirs()
                       val file = File(dir, filname)
                       val fout = FileOutputStream(file)

                    bitmap.compress(Bitmap.CompressFormat.PNG, 85, fout)
                     fout.flush()
                     fout.close()

                    MediaScannerConnection.scanFile(this, arrayOf(file.toString()), null, null)
                    return filePath



    }
    private fun resetTime(){

        mHandles?.removeCallbacks(chonometer)
        timeInSeconds = 0

        var tvTimeData = findViewById<TextView>(R.id.tvTimesData)
        tvTimeData.text =" 00:00"

    }
    private fun starTime(){
        mHandles = Handler(Looper.getMainLooper())
        chonometer.run()

    }

    private  var chonometer : Runnable = object : Runnable{
        override fun run() {
            try {
                if(gaming){
                    timeInSeconds++
                    updateStopWatchView(timeInSeconds)
                }

            } finally {
                mHandles!!.postDelayed(this, 1000L)
            }
        }
    }
    private fun updateStopWatchView(timeInSeconds : Long){

        val formattedTime = getFormattedStopWatch((timeInSeconds * 1000))
        val  tvTimeData = findViewById<TextView>(R.id.tvTimesData)
        tvTimeData.text = formattedTime
    }
    private fun getFormattedStopWatch( ms: Long): String {
        var milliseconds = ms
        val minutes =  TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (minutes < 10) "0" else ""} $minutes: " +
                "${if (seconds < 10) "0" else ""} $seconds "

    }
    private  fun starGame(){

        setLevel()
        setLevelParameters()


        resetBoard()
        clearBoar()

        setBoardLevel()
        setFirstPossition()

        resetTime()
        starTime()
        gaming = true
    }

}