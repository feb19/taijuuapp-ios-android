package jp.feb19.taijuu

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BodyMassGraphView : View {

    var paint = Paint()

    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val bodyMasses = GoogleFit.shared().bodyMasses

        val vw = (width/320).toFloat()

        // 日付を書き込む
        paint.textSize = 13.0F * vw
        paint.color = Color.GRAY
        paint.strokeWidth = 0.0F
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER

        val baseX = 48.0F * vw
        var w = width - (baseX + 32.0F * vw)
        var i = 0
        repeat(7) {
            if (i < bodyMasses.size) {
                val bodyMass = bodyMasses[i]
                canvas.drawText(bodyMass.date, w * (7-i) / 7 + baseX, 200.0F * vw, paint)
                i += 1
            }
        }

        // 平均値、最大値、最小値の算出
        i = 0
        val baseY = 16.0F * vw
        var min = 200.0
        var max = 0.0
        var avg = 0.0
        repeat(7) {
            if (i < bodyMasses.size) {
                if (bodyMasses[i].bodyMass < min) {
                    min = bodyMasses[i].bodyMass
                }
                if (bodyMasses[i].bodyMass > max) {
                    max = bodyMasses[i].bodyMass
                }
                i += 1
                avg += bodyMasses[i].bodyMass
            }
        }
        if (avg > 0.0) {
            avg = avg / i.toFloat()
        }

        // 最大値・最小値・中央値を書き込む
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("${max}kg", 48.0F * vw, 24.0F * vw, paint)
        canvas.drawText("${(max+min)/2}kg",
            48.0F * vw,
            (((176.0F+24.0F)/2.0F)) * vw,
            paint)
        canvas.drawText("${min}kg", 48.0F * vw, 176.0F * vw, paint)

        // 目盛り線を書く
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4.0F
        paint.color = Color.rgb(200, 200, 200)
        canvas.drawLine(0.7F / 7.0F * w + baseX,
            baseY,
            7.3F / 7.0F * w + baseX,
            baseY,
            paint)
        canvas.drawLine(0.7F / 7.0F * w + baseX,
            baseY + (176.0F-24.0F) * vw,
            7.3F / 7.0F * w + baseX,
            baseY + (176.0F-24.0F) * vw,
            paint)

        // 平均ライン
        paint.color = Color.RED
        val per = ((avg - min) / (max - min)).toFloat()
        canvas.drawLine(0.7F / 7.0F * w + baseX,
            baseY + (176.0F-24.0F) * vw * per,
            7.3F / 7.0F * w + baseX,
            baseY + (176.0F-24.0F) * vw * per,
            paint)

        // データをプロットした線グラフ
        paint.color = Color.BLUE
        i = 0
        repeat(6) {
            if (i < bodyMasses.size && i+1 < bodyMasses.size) {
                val bodyMass = bodyMasses[i]
                val bodyMass1 = bodyMasses[i + 1]
                var h = (176.0F-24.0F) * vw
                val n = h - (bodyMasses[i].bodyMass - min) / (max-min) * h
                val n1 = h - (bodyMasses[i+1].bodyMass - min) / (max-min) * h

                canvas.drawLine(w * (7-i) / 7 + baseX,
                    (n + baseY).toFloat(),
                    w * (7-i-1) / 7 + baseX,
                    (n1 + baseY).toFloat(),
                    paint)
                i += 1
            }
        }
    }
}