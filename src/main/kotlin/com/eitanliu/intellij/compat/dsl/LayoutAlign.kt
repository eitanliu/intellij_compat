package com.eitanliu.intellij.compat.dsl

import com.intellij.ui.layout.CCFlags
import com.intellij.ui.layout.CellBuilder
import javax.swing.JComponent

fun <C : JComponent> CellBuilder<C>.layoutAlign(
    align: LayoutAlign
): CellBuilder<C> {
    try {
        layoutAlignBefore203(align)
    } catch (e: Throwable) {
    }
    return this
}

private fun <C : JComponent> CellBuilder<C>.layoutAlignBefore203(
    align: LayoutAlign
): CellBuilder<C> {
    when (align) {
        is LayoutAlignBoth -> {
            when {
                align.alignX == LayoutAlignX.FILL && align.alignY == LayoutAlignY.FILL -> {
                    constraints(CCFlags.push)
                }

                align.alignX == LayoutAlignX.CENTER && align.alignY == LayoutAlignY.CENTER -> {
                    constraints(CCFlags.grow)
                }

                else -> {
                    val horizontalAlign = when (align.alignX) {
                        LayoutAlignX.FILL -> CCFlags.pushX
                        else -> CCFlags.growX
                    }
                    val verticalAlign = when (align.alignY) {
                        LayoutAlignY.FILL -> CCFlags.pushY
                        else -> CCFlags.growY
                    }
                    constraints(horizontalAlign, verticalAlign)
                }
            }
        }

        is LayoutAlignX -> {
            val horizontalAlign = when (align) {
                LayoutAlignX.FILL -> CCFlags.pushX
                else -> CCFlags.growX
            }
            constraints(horizontalAlign)
        }

        is LayoutAlignY -> {
            val verticalAlign = when (align) {
                LayoutAlignY.FILL -> CCFlags.pushY
                else -> CCFlags.growY
            }
            constraints(verticalAlign)
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