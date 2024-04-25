package com.mxcsyounes.realmkotlin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.mxcsyounes.realmkotlin.ui.theme.RealmKotlinTheme
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  lateinit var realm: Realm
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    realm = dbSetup()
    setContent {
      val scope = rememberCoroutineScope()
      RealmKotlinTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Column(Modifier.padding(innerPadding)) {
            Text(
              text = "Tap to Add Data",
              modifier = Modifier.clickable {
                scope.launch {
                  addData()
                  delay(3000)
                  showDataNormally()
                }
              }
            )
          }
        }
      }
    }
  }

  private fun addData() {
    val person = Person().apply {
      name = "Carlo"
      dog = Dog().apply { name = "Fido"; age = 16 }
    }

    CoroutineScope(Dispatchers.IO).launch {
      realm.write {
        copyToRealm(person)
      }
    }
  }

  fun dbSetup(): Realm {
    val configuration = RealmConfiguration.create(schema = setOf(Person::class, Dog::class))
    return Realm.open(configuration)
  }

  fun showDataAsFlow() {
    CoroutineScope(Dispatchers.IO).launch {
      val result = realm.query(Person::class).count()
      result.asFlow().collect {
        Log.d("Test", "Count is ${it}")
      }
    }
  }

  fun showDataNormally() {
    CoroutineScope(Dispatchers.IO).launch {
      val result = realm.query(Person::class).count()
      Log.d("Test", "Count is ${result.find()}")
    }
  }
}

class Person : RealmObject {
  var name: String = "Foo"
  var dog: Dog? = null
}

class Dog : RealmObject {
  var name: String = ""
  var age: Int = 0
}