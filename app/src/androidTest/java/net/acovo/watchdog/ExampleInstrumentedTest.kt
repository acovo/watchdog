package net.acovo.watchdog

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private val TAG = "ExampleInstrumentedTest"
    private val mContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private var mCounter:GuardService.CounterBinder? = null;
    private val mConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            Log.d(TAG,"onServiceConnected")
            mCounter = p1 as GuardService.CounterBinder
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d(TAG,"onServiceDisconnected")
        }
    }

    @Test
    fun useAppContext() {

        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("net.acovo.watchdog", appContext.packageName)

        var intent = Intent(mContext,GuardService::class.java)
        mContext.bindService(intent,mConnection,Context.BIND_AUTO_CREATE)
        TimeUnit.SECONDS.sleep(1)

        var result = mCounter?.getCount()
        Log.d(TAG,String.format("result1 %d",result))

        result = mCounter?.getCount()
        Log.d(TAG,String.format("result2 %d",result))

        mCounter?.getService()?.startTask("test")
    }
}