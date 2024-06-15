package com.example.unievents.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.unievents.data.Event
import com.example.unievents.data.User

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.weight(1f)) { // Make title occupy full width
                Text(event.name, style = MaterialTheme.typography.headlineSmall)
                Text(event.location, style = MaterialTheme.typography.bodyMedium)
                Text(text = "Date: ${event.date}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Time: ${event.time}", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.width(8.dp)) // Add spacing between columns
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(text = "Organizer: ${event.organizer}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "${event.attendeesCount} / ${event.capacity} Users", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
