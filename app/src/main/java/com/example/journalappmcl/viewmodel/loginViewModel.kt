import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.core.content.edit

class LoginViewModel(application: Application) : AndroidViewModel(application) {
  private val prefs = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
  val isLoggedIn = MutableLiveData<Boolean>(prefs.getBoolean("isLoggedIn", false))

  fun completeLogin() {
    prefs.edit() { putBoolean("isLoggedIn", true) }
    isLoggedIn.value = true
  }

  fun logout() {
    prefs.edit() { clear() }
    isLoggedIn.value = false
  }
}
