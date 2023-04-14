package firebase

import model.StartPoint

interface StartPointCallback {
    fun onStartPointCallBack(startPoints: List<StartPoint>)
}