@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package com.eitanliu.intellij.compat.dsl

import com.intellij.ui.layout.CellBuilder
import javax.swing.JComponent
import java.lang.Enum as JEnum

fun <C : JComponent> CellBuilder<C>.layoutAlign(
    align: LayoutAlign
): CellBuilder<C> {
    try {
        layoutAlignBefore203(align)
    } catch (e: Throwable) {
    }
    return this
}

@Suppress("UNCHECKED_CAST")
private fun <C : JComponent> CellBuilder<C>.layoutAlignBefore203(
    align: LayoutAlign
): CellBuilder<C> {
    val clazz = Class.forName("com.intellij.ui.layout.CCFlags")
    val enumClass = clazz as Class<out Enum<*>>
    val cellClass = this::class.java
    val alignMethod = cellClass.methods.first { it.name == "constraints" }
    when (align) {
        is LayoutAlignBoth -> {

            when {
                align.alignX == LayoutAlignX.FILL && align.alignY == LayoutAlignY.FILL -> {
                    val flag = JEnum.valueOf(enumClass, "push")
                    alignMethod.invoke(this, flag)
                }

                align.alignX == LayoutAlignX.CENTER && align.alignY == LayoutAlignY.CENTER -> {
                    val flag = JEnum.valueOf(enumClass, "grow")
                    alignMethod.invoke(this, flag)
                }

                else -> {
                    val horizontalAlign = when (align.alignX) {
                        LayoutAlignX.FILL -> JEnum.valueOf(enumClass, "pushX")
                        else -> JEnum.valueOf(enumClass, "growX")
                    }
                    val verticalAlign = when (align.alignY) {
                        LayoutAlignY.FILL -> JEnum.valueOf(enumClass, "pushY")
                        else -> JEnum.valueOf(enumClass, "growY")
                    }
                    alignMethod.invoke(this, horizontalAlign, verticalAlign)
                }
            }
        }

        is LayoutAlignX -> {
            val horizontalAlign = when (align) {
                LayoutAlignX.FILL -> JEnum.valueOf(enumClass, "pushX")
                else -> JEnum.valueOf(enumClass, "growX")
            }
            alignMethod.invoke(this, horizontalAlign)
        }

        is LayoutAlignY -> {
            val verticalAlign = when (align) {
                LayoutAlignY.FILL -> JEnum.valueOf(enumClass, "pushY")
                else -> JEnum.valueOf(enumClass, "growY")
            }
            alignMethod.invoke(this, verticalAlign)
        }
    }
    return this
}

/**
 * https://plugins.jetbrains.com/docs/intellij/kotlin-ui-dsl-version-2.html#cell-align
 * since 2022.3 align(AlignX + AlignY) [Align.kt](https://github.com/JetBrains/intellij-community/blob/223.7126/platform/platform-impl/src/com/intellij/ui/dsl/builder/Align.kt)
 * before 2022.3 horizontalAlign(HorizontalAlign), verticalAlign(VerticalAlign) [Constraints.kt](https://github.com/JetBrains/intellij-community/blob/223.7126/platform/platform-impl/src/com/intellij/ui/dsl/gridLayout/Constraints.kt)
 */
sealed interface LayoutAlign {
    companion object {
        @JvmField
        val FILL: LayoutAlign = LayoutAlignX.FILL + LayoutAlignY.FILL

        @JvmField
        val CENTER: LayoutAlign = LayoutAlignX.CENTER + LayoutAlignY.CENTER
    }
}

sealed interface LayoutAlignX : LayoutAlign {
    object LEFT : LayoutAlignX
    object CENTER : LayoutAlignX
    object RIGHT : LayoutAlignX
    object FILL : LayoutAlignX
}

sealed interface LayoutAlignY : LayoutAlign {
    object TOP : LayoutAlignY
    object CENTER : LayoutAlignY
    object BOTTOM : LayoutAlignY
    object FILL : LayoutAlignY
}

operator fun LayoutAlignX.plus(alignY: LayoutAlignY): LayoutAlign {
    return LayoutAlignBoth(this, alignY)
}

operator fun LayoutAlignY.plus(alignX: LayoutAlignX): LayoutAlign {
    return LayoutAlignBoth(alignX, this)
}

internal class LayoutAlignBoth(val alignX: LayoutAlignX, val alignY: LayoutAlignY) : LayoutAlign