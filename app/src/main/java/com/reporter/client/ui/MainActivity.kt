package com.reporter.client.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.reporter.util.model.Teller
import com.reporter.util.ui.AbstractActivity

class MainActivity : AbstractActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        Teller.warn("teller is working!")
        setContent {
            Text("reporter in action!")
        }
    }
}