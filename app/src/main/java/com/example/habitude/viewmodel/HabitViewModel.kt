package com.example.habitude.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.habitude.data.Habit
import com.example.habitude.utils.Constants.HABIT_COLLECTION
import com.example.habitude.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HabitViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {

    private val _addHabit = MutableStateFlow<Resource<Habit>>(Resource.Unspecified())
    val addHabit: Flow<Resource<Habit>> = _addHabit

    private val _habits = MutableStateFlow<Resource<ArrayList<Habit>>>(Resource.Unspecified())
    val habits: Flow<Resource<ArrayList<Habit>>> = _habits

    fun saveHabit(habit: Habit) {
        viewModelScope.launch { _addHabit.emit(Resource.Loading()) }

        val userId = firebaseAuth.currentUser?.uid

        val query = db.collection(HABIT_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("name", habit.name)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    val updatedHabit = habit.copy(userId = userId.orEmpty())

                    db.collection(HABIT_COLLECTION)
                        .add(updatedHabit)
                        .addOnSuccessListener {
                            _addHabit.value = Resource.Success(habit)
                        }.addOnFailureListener {
                            _addHabit.value = Resource.Error(it.message.toString())
                        }
                } else {
                    _addHabit.value = Resource.Error("A habit with the same name already exists.")
                }
            }.addOnFailureListener {
                _addHabit.value = Resource.Error(it.message.toString())
            }
    }

    fun getHabits() {
        viewModelScope.launch { _habits.emit(Resource.Loading()) }

        val userId = firebaseAuth.currentUser?.uid

        val query = db.collection(HABIT_COLLECTION)
            .whereEqualTo("userId", userId)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val habitList = mutableListOf<Habit>()
                for (document in querySnapshot) {
                    val habit = document.toObject(Habit::class.java)
                    habitList.add(habit)
                }
                _habits.value = Resource.Success(ArrayList(habitList))
            }.addOnFailureListener {
                _habits.value = Resource.Error(it.message.toString())
            }
    }
}