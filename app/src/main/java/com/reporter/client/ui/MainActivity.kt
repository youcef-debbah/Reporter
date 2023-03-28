@file:OptIn(ExperimentalMaterial3Api::class)

package com.reporter.client.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import com.reporter.util.model.Teller
import com.reporter.util.ui.AbstractActivity
import com.reporter.util.ui.SimpleScaffold

class MainActivity : AbstractActivity() {

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        setContent {
            SimpleScaffold {
                Teller.info("teller in action!")
                Text("reporter in action!")
            }
        }
    }
}