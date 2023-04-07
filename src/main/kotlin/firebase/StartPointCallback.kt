package firebase

import model.StartPoint

interface StartPointCallback {
    fun onStartPointCallBack(data: List<StartPoint>)
}