package com.reporter.util.model

import androidx.recyclerview.widget.DiffUtil.ItemCallback

interface VisualItem {

    fun id(): Any

    fun areItemsTheSame(other: VisualItem): Boolean =
        other::class == this::class && other.id() == this.id()

    fun areContentsTheSame(other: VisualItem): Boolean =
        other::class == this::class && other.id() == this.id()

    fun getChangePayload(): Any? = id()

    companion object {

        fun <T : VisualItem> diffCallback(): ItemCallback<T> =
            object : ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean =
                    oldItem.areItemsTheSame(newItem)

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
                    oldItem.areContentsTheSame(newItem)

                override fun getChangePayload(oldItem: T, newItem: T): Any? =
                    newItem.getChangePayload()
            }

        fun <T : Any, C1 : Any, C2 : Any> compDiff(
            splitter: (T) -> Pair<C1, C2>,
            firstDiff: ItemCallback<C1>,
            secondDiff: ItemCallback<C2>
        ): ItemCallback<T> =
            object : ItemCallback<T>() {
                override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
                    val oldParts = splitter(oldItem)
                    val newParts = splitter(newItem)
                    return firstDiff.areItemsTheSame(oldParts.first, newParts.first)
                            && secondDiff.areItemsTheSame(oldParts.second, newParts.second)
                }

                override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
                    val oldParts = splitter(oldItem)
                    val newParts = splitter(newItem)
                    return firstDiff.areContentsTheSame(oldParts.first, newParts.first)
                            && secondDiff.areContentsTheSame(oldParts.second, newParts.second)
                }

                override fun getChangePayload(oldItem: T, newItem: T): Any? {
                    val oldParts = splitter(oldItem)
                    val newParts = splitter(newItem)
                    val firstPayload = firstDiff.getChangePayload(oldParts.first, newParts.first)
                    val secondPayload =
                        secondDiff.getChangePayload(oldParts.second, newParts.second)
                    return if (firstPayload == null && secondPayload == null)
                        null
                    else if (firstPayload == null)
                        secondPayload
                    else if (secondPayload == null)
                        firstPayload
                    else
                        listOf(firstPayload, secondPayload)
                }
            }

        val STRING_DIFF = object : ItemCallback<String>() {
            override fun areItemsTheSame(
                firstItem: String,
                secondItem: String,
            ): Boolean = firstItem == secondItem

            override fun areContentsTheSame(
                firstItem: String,
                secondItem: String,
            ): Boolean = firstItem == secondItem

            override fun getChangePayload(oldItem: String, newItem: String): Any? {
                return newItem
            }
        }

        val NUMBER_DIFF = object : ItemCallback<Number>() {
            override fun areItemsTheSame(
                firstItem: Number,
                secondItem: Number,
            ): Boolean = firstItem == secondItem

            override fun areContentsTheSame(
                firstItem: Number,
                secondItem: Number,
            ): Boolean = firstItem == secondItem

            override fun getChangePayload(oldItem: Number, newItem: Number): Any {
                return newItem
            }
        }
    }
}
