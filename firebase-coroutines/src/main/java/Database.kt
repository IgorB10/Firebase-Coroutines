package com.igorbykov.firebase_database_coroutines

import com.google.firebase.FirebaseException
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private suspend fun <T : Any> readObject(
    reference: DatabaseReference,
    type: Class<T>
): T = suspendCancellableCoroutine { continuation ->
    val listener = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            continuation.resumeWithException(error.toException())
        }
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val data: T? = snapshot.getValue(type)
                continuation.resume(data!!)
            } catch (exception: Exception) {
                continuation.resumeWithException(exception)
            }
        }
    }
    continuation.invokeOnCancellation { reference.removeEventListener(listener) }
    reference.addListenerForSingleValueEvent(listener)
}

suspend fun <T : Any> DatabaseReference.readValue(type: Class<T>): T = readObject(this, type)

suspend inline fun <reified T : Any> DatabaseReference.readValue(): T = readValue(T::class.java)

suspend fun <T : Any> DatabaseReference.saveValue(value: T) = setValue(value).await()

suspend fun <T : Any> DatabaseReference.saveValue(key: String, value: T) = setValue(value, key).await()

suspend fun <T : Any> DatabaseReference.pushValue(value: T) = push().saveValue(value)

private suspend fun <T : Any> awaitQuerySingleValue(query: Query, type: Class<T>): T =
    suspendCancellableCoroutine { continuation ->
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) = try {
                continuation.resume(snapshot.getValue(type)!!)
            } catch (exception: Exception) {
                continuation.resumeWithException(exception)
            }

            override fun onCancelled(error: DatabaseError) =
                continuation.resumeWithException(error.toException())
        }

        query.addListenerForSingleValueEvent(listener)
        continuation.invokeOnCancellation { query.removeEventListener(listener) }
    }

suspend fun <T : Any> Query.readValue(type: Class<T>): T = awaitQuerySingleValue(this, type)

suspend inline fun <reified T : Any> Query.readValue(): T = readValue(T::class.java)

suspend fun <T : Any> Query.subscribeOnDataChange(type: Class<T>): Flow<T> = subscribeDataChange(this, type)

private suspend fun <T : Any> subscribeDataChange(query: Query, type: Class<T>): Flow<T> {
    return callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                trySend(snapshot.getValue(type)!!)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        query.addValueEventListener(listener)
        awaitClose {
            query.removeEventListener(listener)
        }
    }
}

