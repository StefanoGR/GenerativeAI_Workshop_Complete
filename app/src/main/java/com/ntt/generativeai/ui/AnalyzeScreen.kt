package com.ntt.generativeai.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun AnalyzeScreen(modifier: Modifier, scanned: List<File>) {
    var ok by remember { mutableStateOf(false) }
    val map = scanned.mapIndexed { index, file -> index to file }.toMap()
    val analized = remember { mutableMapOf<Int, String>() }
    val files = LocalContext.current.filesDir
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            map.map { (i, file) ->
                launch {
                    val image = InputImage.fromBitmap(file.getBitmap(), 0)

                    recognizer.process(image)
                        .addOnSuccessListener { visionText ->
                            // Task completed successfully
                            // ...
                            //Log.e("TEXT", visionText.text)
                            analized[i] = visionText.text
                        }
                        .addOnFailureListener { e ->
                            // Task failed with an exception
                            // ...
                            analized[i] = e.message.toString()
                        }
                }
            }.forEach { it.join() }
            delay(3000)
            // TODO ora che ho il testo per ogni foglio devo generare il riassunto
            ok = true
        }
    }

    Column(modifier = modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(15.dp)
        .verticalScroll(rememberScrollState())) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Analizzo ${scanned.size} fogli", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 20.sp)
        }
        Spacer(Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), Arrangement.Center) {
            if (!ok) CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            else Icon(
                Icons.Rounded.CheckCircle, "", modifier = Modifier.width(64.dp)
            )
        }
        Spacer(Modifier.height(20.dp))
        if (ok) {
            Row(modifier = Modifier.fillMaxWidth(), Arrangement.Center) {
                Text("Riassunto: ***\n${analized.toSortedMap().map { "Pagina ${it.key + 1}:\n" + it.value }.joinToString("\n---------\n")}")
            }
            Row(modifier = Modifier.fillMaxWidth(), Arrangement.Center) {
                Button(
                    onClick = {
                        salvaImmaginiInPdf(scanned.map { it.getBitmap() }, files)
                    },
                    modifier = Modifier
                        .padding(16.dp),
                    shape = ButtonDefaults.outlinedShape
                ) {
                    Text("Salva PDF")
                }
            }
        }
    }
}

@Preview
@Composable
private fun Prev() {
    AnalyzeScreen(Modifier, listOf(File("")))
}

private fun salvaImmaginiInPdf(bitmaps: List<Bitmap>, filesDir: File) {
    val pdfDocument = PdfDocument()
    for (bitmap in bitmaps) {
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        // Disegna il bitmap nella pagina del PDF
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)
    }

    // Salva il PDF in un file
    val file = File(filesDir, "documento_scansionato.pdf")
    pdfDocument.writeTo(file.outputStream())
    pdfDocument.close()

    //Toast.makeText(context, "PDF salvato in ${file.absolutePath}", Toast.LENGTH_LONG).show()
}

fun File.getBitmap() = BitmapFactory.decodeFile(absolutePath)