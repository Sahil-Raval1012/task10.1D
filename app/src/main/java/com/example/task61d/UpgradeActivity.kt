package com.example.task61d
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.task61d.data.UserSession
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
class UpgradeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        if (UserSession.isPremium) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Already Premium!")
                .setMessage("You already have a premium account. Enjoy all the features!")
                .setPositiveButton("OK") { _, _ -> finish() }
                .setCancelable(false)
                .show()
            return
        }
        findViewById<Button>(R.id.btnStarter).setOnClickListener {
            showGooglePay("Starter Plan", "$2.99/month")
        }
        findViewById<Button>(R.id.btnIntermediate).setOnClickListener {
            showGooglePay("Intermediate Plan", "$4.99/month")
        }
        findViewById<Button>(R.id.btnAdvanced).setOnClickListener {
            showGooglePay("Advanced Plan", "$9.99/month")
        }
    }
    private fun showGooglePay(planName: String, price: String) {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_google_pay, null)
        view.findViewById<TextView>(R.id.tvPaymentPlan).text =
            "Subscribe to $planName\n$price"
        view.findViewById<Button>(R.id.btnPayNow).setOnClickListener {
            dialog.dismiss()
            processPurchase(planName)
        }
        dialog.setContentView(view)
        dialog.show()
    }
    private fun processPurchase(planName: String) {
        UserSession.isPremium = true
        MaterialAlertDialogBuilder(this)
            .setTitle("Welcome to Premium! 🎉")
            .setMessage("You have successfully subscribed to $planName.\n\nEnjoy unlimited quizzes, AI-powered study plans, and detailed analytics!")
            .setPositiveButton("Start Learning") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }
}
