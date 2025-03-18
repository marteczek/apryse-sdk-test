package pl.infinite.aprysesdktest

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pdftron.pdf.Field
import com.pdftron.pdf.PDFDoc

class MainActivity : AppCompatActivity() {
    private var textViewResult: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val buttonDoc1 = findViewById<Button>(R.id.buttonDoc1)
        val buttonDoc2 = findViewById<Button>(R.id.buttonDoc2)
        textViewResult = findViewById(R.id.textViewResult)
        buttonDoc1.setOnClickListener { v -> processDoc1()}
        buttonDoc2.setOnClickListener { v -> processDoc2()}
    }

    private fun processDoc1() {
        textViewResult?.let {
            it.text = readRadioGroupsElements("documentRadioTest1.pdf")
        }
    }

    private fun processDoc2() {
        textViewResult?.let {
            it.text = readRadioGroupsElements("documentRadioTest2.pdf")
        }
    }

    // For simplicity we skip working thread, view model, etc.
    private fun readRadioGroupsElements(fileName: String): String {
        try {
            assets.open(fileName).use { pdfStream ->
                val sb = StringBuilder()
                val doc = PDFDoc(pdfStream)
                val itr = doc.fieldIterator
                while (itr.hasNext()) {
                    val field = itr.next()!!
                    if (field.type == Field.e_radio) {
                        val apNItr = field.sdfObj.get("AP").value().get("N").value().dictIterator
                        while (apNItr.hasNext()) {
                            apNItr.key()
                            val name = apNItr.key().name //When expected name is Wybór1 application crashes
                            sb.append("Group: ${field.name}, Button: $name\n")
                            Log.i("PdfTronTest" ,"AP/N name $name")
                            apNItr.next()
                        }
                    }
                }
                doc.close()
                return sb.toString()
            }
        } catch (e: Exception) {
            Log.e("PdfTronTest", "Exception ${e.message}")
            return "Error msg: ${e.message}"
        }

    }
}