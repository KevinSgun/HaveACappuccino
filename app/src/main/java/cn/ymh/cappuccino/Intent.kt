package cn.ymh.cappuccino

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.io.Serializable

inline fun <reified T : Activity> Context.startActivity(
    vararg params: Pair<String, Any?>
) {
    internalStartActivity(this, T::class.java, params, null)
}

inline fun <reified T : Activity> Context.startActivity(
    options: Bundle,
    vararg params: Pair<String, Any?>
) {
    internalStartActivity(this, T::class.java, params, options)
}


inline fun <reified T : Activity> Fragment.startActivity(
    vararg params: Pair<String, Any?>
) {
    internalStartActivity(activity!!, T::class.java, params, null)
}

inline fun <reified T : Activity> Fragment.startActivity(
    options: Bundle,
    vararg params: Pair<String, Any?>
) {
    internalStartActivity(activity!!, T::class.java, params, options)
}


inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    vararg params: Pair<String, Any?>,
    needAnimation: Boolean = true
) {
    internalStartActivityForResult(this, T::class.java, requestCode, params, Bundle())
}

inline fun <reified T : Activity> Activity.startActivityForResult(
    requestCode: Int,
    options: Bundle,
    vararg params: Pair<String, Any?>,
    needAnimation: Boolean = true
) {
    internalStartActivityForResult(this, T::class.java, requestCode, params, options)
}

inline fun <reified T : Activity> Fragment.startActivityForResult(
    requestCode: Int,
    vararg params: Pair<String, Any?>,
    needAnimation: Boolean = true
) {
    internalStartActivityForResult(this, T::class.java, requestCode, params, Bundle())
}

inline fun <reified T : Activity> Fragment.startActivityForResult(
    requestCode: Int,
    options: Bundle,
    vararg params: Pair<String, Any?>
) {
    internalStartActivityForResult(this, T::class.java, requestCode, params, options)
}

fun internalStartActivity(
    ctx: Context,
    activity: Class<out Activity>,
    params: Array<out Pair<String, Any?>>,
    options: Bundle? = null
) {
    ctx.startActivity(createIntent(ctx, activity, params), options)
}

fun internalStartActivityForResult(
    act: Activity,
    activity: Class<out Activity>,
    requestCode: Int,
    params: Array<out Pair<String, Any?>>,
    options: Bundle? = null
) {
    act.startActivityForResult(createIntent(act, activity, params), requestCode, options)
}

fun internalStartActivityForResult(
    fragment: Fragment,
    activity: Class<out Activity>,
    requestCode: Int,
    params: Array<out Pair<String, Any?>>,
    options: Bundle? = null
) {
    fragment.startActivityForResult(
        createIntent(fragment.context!!, activity, params),
        requestCode,
        options
    )
}

/**
 * 构建Intent
 */
private fun <T> createIntent(
    ctx: Context,
    clazz: Class<out T>,
    params: Array<out Pair<String, Any?>>
): Intent {
    val intent = Intent(ctx, clazz)
    if (params.isNotEmpty()) fillIntentArguments(intent, params)
    return intent
}

private fun fillIntentArguments(intent: Intent, params: Array<out Pair<String, Any?>>) {
    params.forEach {
        when (val value = it.second) {
            null -> intent.putExtra(it.first, null as Serializable?)
            is Boolean -> intent.putExtra(it.first, value)
            is String -> intent.putExtra(it.first, value)
            is CharSequence -> intent.putExtra(it.first, value)
            is Parcelable -> {
//                intent.setExtrasClassLoader(it::class.java.classLoader)
                intent.putExtra(it.first, value)
            }
            is Array<*> -> when {
                value.isArrayOf<CharSequence>() -> intent.putExtra(it.first, value)
                value.isArrayOf<String>() -> intent.putExtra(it.first, value)
                value.isArrayOf<Parcelable>() -> intent.putExtra(it.first, value)
                else -> throw RuntimeException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
            }
            is Serializable -> intent.putExtra(it.first, value)
            is Int -> intent.putExtra(it.first, value)
            is Long -> intent.putExtra(it.first, value)
            is Float -> intent.putExtra(it.first, value)
            is Double -> intent.putExtra(it.first, value)
            is Char -> intent.putExtra(it.first, value)
            is Short -> intent.putExtra(it.first, value)
            is Bundle -> intent.putExtra(it.first, value)
            is IntArray -> intent.putExtra(it.first, value)
            is LongArray -> intent.putExtra(it.first, value)
            is FloatArray -> intent.putExtra(it.first, value)
            is DoubleArray -> intent.putExtra(it.first, value)
            is CharArray -> intent.putExtra(it.first, value)
            is ShortArray -> intent.putExtra(it.first, value)
            is BooleanArray -> intent.putExtra(it.first, value)
            else -> throw RuntimeException("Intent extra ${it.first} has wrong type ${value.javaClass.name}")
        }
        return@forEach
    }
}