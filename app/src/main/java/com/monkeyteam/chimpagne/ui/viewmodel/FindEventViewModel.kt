package com.monkeyteam.chimpagne.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OverviewViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val firebaseConnection = FirebaseConnection()
    private val _todos = MutableStateFlow<List<ToDo>>(emptyList())
    val todos: StateFlow<List<ToDo>> = _todos
    private val _filteredTodos = MutableStateFlow<List<ToDo>>(emptyList())
    val filteredTodos: StateFlow<List<ToDo>> = _filteredTodos
    private val _searchQuery = MutableStateFlow("")

    init {
        fetchTodos()
    }

    private fun fetchTodos() {
        viewModelScope.launch {
            try {
                firebaseConnection.getToDoList { toDoList ->
                    _todos.value = toDoList
                    filterTodos(_searchQuery.value)
                    _isLoading.value = false  // Data fetched, loading complete
                }
            } catch (e: Exception) {
                _isLoading.value = false  // Handle error, loading complete
                // Log or handle the error
            }
        }
    }

    fun filterTodos(query: String) {
        _searchQuery.value = query
        _filteredTodos.value = _todos.value.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
    }

    fun addToDoItem(toDo: ToDo) {
        val updatedList = _todos.value.toMutableList().apply {
            add(toDo)
        }
        _todos.value = updatedList
        filterTodos(_searchQuery.value) // Update the filtered list as well
    }

    fun findToDoById(uid: String?): ToDo? {
        return _todos.value.firstOrNull { it.uid == uid }
    }

    fun updateTodo(updatedTodo: ToDo) {
        // Create a new list to ensure thread safety when updating the state
        val updatedList = _todos.value.toMutableList().apply {
            val index = indexOfFirst { it.uid == updatedTodo.uid }
            if (index != -1) {
                this[index] = updatedTodo
            }
        }
        // Update the state with the new list
        _todos.value = updatedList
        // Reapply any filters or transformations
        filterTodos(_searchQuery.value)
    }

    fun deleteTodoById(uid: String) {
        val updatedList = _todos.value.toMutableList().apply {
            removeAll { it.uid == uid }
        }
        _todos.value = updatedList
        filterTodos(_searchQuery.value)
    }

}