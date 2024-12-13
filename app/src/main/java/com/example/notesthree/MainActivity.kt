package com.example.notesthree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notesthree.ui.theme.NotesThreeTheme
import java.util.UUID


data class NoteItem(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var text: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotesThreeTheme {
                NoteApp()
            }
        }
    }
}


@Composable
fun NoteApp() {
    val navController = rememberNavController()
    val notesList = remember { mutableStateListOf<NoteItem>() }

    // delete a note from list lambda function, passed as an argument in EditNoteScreen
    // -> unit is void return
    val removeNote: (NoteItem) -> Unit = { note ->
        notesList.remove(note)
    }


    NavHost(navController = navController, startDestination = "Notes") {
        composable("Notes") { NoteListScreen(navController, notesList) }
        composable("AddNote") { AddNoteScreen(navController, notesList) }
        composable("EditNoteScreen/{noteID}") { backStackEntry ->
            val noteID = backStackEntry.arguments?.getString("noteID") ?: return@composable // gets id
            val noteItem = notesList.find { it.id == noteID } // finds to display correct note
            if (noteItem != null) { // if it's not null return it
                EditNoteScreen(navController, noteItem, removeNote)
            }
        }

    }
}

// Screen for viewing our notes, redirect in NoteApp
// scaffold is a core layout, floatingactionbutton has no placement e.g.
// items is a function, list display
// dynamic ui
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(navController: NavController, noteList: MutableList<NoteItem>) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Very Basic Notes App") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("AddNote") }) {
                Icon(Icons.Filled.Add, contentDescription = "Add Note")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
        ) {
            items(noteList) { note ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { navController.navigate("EditNoteScreen/${note.id}") }
                        .border(1.dp, MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = note.title, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}


// Method for validation instead of repeating the code within other functions
// parameter title:String, returns string or null (?)
fun validateTitle(title: String): String? {
    return when {
        title.length < 3 -> "Title must be at least 3 characters long"
        title.length > 50 -> "Title must be at most 50 characters long"
        else -> null
    }
}

fun validateContent(text: String): String? {
    return if (text.length > 120) "Text must be at most 120 characters long" else null
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(navController: NavController, noteList: MutableList<NoteItem>) {
    var title by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Note") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = validateTitle(it)
                },
                label = { Text("Title") },
                isError = titleError != null
            )
            titleError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = text,
                onValueChange = {
                    text = it
                    contentError = validateContent(it)
                },
                label = { Text("Content") },
                isError = contentError != null
            )
            contentError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                titleError = validateTitle(title)
                contentError = validateContent(text)

                if (titleError == null && contentError == null) {
                    noteList.add(NoteItem(title = title, text = text)) // ID generated automatically
                    navController.popBackStack()
                }
            }) {
                Text("Add Note")
            }
        }
    }
}

// Edit note screen, redirect used in NoteApp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteScreen(navController: NavController, note: NoteItem, removeNote: (NoteItem) -> Unit) {
    var title by remember { mutableStateOf(note.title) }
    var text by remember { mutableStateOf(note.text) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var contentError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Edit Note") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = validateTitle(it)
                },
                label = { Text("Title") },
                isError = titleError != null
            )
            titleError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = text,
                onValueChange = {
                    text = it
                    contentError = validateContent(it)
                },
                label = { Text("Content") },
                isError = contentError != null
            )
            contentError?.let { Text(text = it, color = MaterialTheme.colorScheme.error) }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    titleError = validateTitle(title)
                    contentError = validateContent(text)

                    if (titleError == null && contentError == null) {
                        note.title = title
                        note.text = text
                        navController.popBackStack()
                    }
                }) {
                    Text("Save Changes")
                }

                Button(onClick = {
                    removeNote(note)
                    navController.popBackStack()
                }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete Note")
                }
            }
        }
    }
}




