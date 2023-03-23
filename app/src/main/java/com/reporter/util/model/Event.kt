package com.reporter.util.model

import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Represent a GA4 [event](https://developers.google.com/analytics/devguides/collection/ga4/reference/events)
 *
 * consider for future use:
 *
 *  search
 *  tutorial_begin
 *  tutorial_complete
 *  select_promotion
 *  view_promotion
 */
@Suppress("unused")
object Event {

    // common values
    object Trigger {
        const val CLICK_CONTROLLER = "click_controller"
        const val CLEAR_NOTIFICATION = "clear_notification"
        const val CLICK_NOTIFICATION_ACTION = "click_notification_action"
    }

    // common types
    object ItemParam {
        /**
         * The ID of the item.
         *
         *
         * Name: item_id
         *
         *
         * Type: string
         *
         *
         * Required: yes (unless item_name is set)
         *
         *
         * Example: SKU_12345
         */
        const val ITEM_ID: String = FirebaseAnalytics.Param.ITEM_ID

        /**
         * The name of the item.
         *
         *
         * Name: item_name
         *
         *
         * Type: string
         *
         *
         * Required: yes (unless item_id is set)
         *
         *
         * Example: Black Coat
         */
        const val ITEM_NAME: String = FirebaseAnalytics.Param.ITEM_NAME

        /**
         * A product affiliation to designate a supplying company or brick and mortar store location.
         *
         *
         * Event-level and item-level affiliation parameters are independent.
         *
         *
         * Name: affiliation
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: Google Store
         */
        const val AFFILIATION: String = FirebaseAnalytics.Param.AFFILIATION

        /**
         * The coupon name/code associated with the event.
         *
         *
         * Event-level and item-level coupon parameters are independent.
         *
         *
         * Name: coupon
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: SUMMER_FUN
         */
        const val COUPON: String = FirebaseAnalytics.Param.COUPON

        /**
         * The currency, in 3-letter ISO 4217 format.
         *
         *
         * If set, event-level currency is ignored.
         *
         *
         * Multiple currencies per event is not supported. Each item should set the same currency.
         *
         *
         * Name: currency
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: USD
         */
        const val CURRENCY: String = FirebaseAnalytics.Param.CURRENCY

        /**
         * The monetary discount value associated with the item.
         *
         *
         * Name: discount
         *
         *
         * Type: number
         *
         *
         * Required: no
         *
         *
         * Example: 1.25
         */
        const val DISCOUNT: String = FirebaseAnalytics.Param.DISCOUNT

        /**
         * The index/position of the item in a list.
         *
         *
         * Name: index
         *
         *
         * Type: number
         *
         *
         * Required: no
         *
         *
         * Example: 5
         */
        const val INDEX: String = FirebaseAnalytics.Param.INDEX

        /**
         * The brand of the item.
         *
         *
         * Name: item_brand
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: Google
         */
        const val ITEM_BRAND: String = FirebaseAnalytics.Param.ITEM_BRAND

        /**
         * The category of the item. If used as part of a category hierarchy or taxonomy
         * then this will be the first category.
         *
         *
         * Name: item_category
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: Apparel
         */
        const val ITEM_CATEGORY: String = FirebaseAnalytics.Param.ITEM_CATEGORY

        /**
         * The second category hierarchy or additional taxonomy for the item.
         *
         *
         * Name: item_category2
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: Adult
         */
        const val ITEM_CATEGORY_2: String = FirebaseAnalytics.Param.ITEM_CATEGORY2

        /**
         * The third category hierarchy or additional taxonomy for the item.
         *
         *
         * Name: item_category3
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: Crew
         */
        const val ITEM_CATEGORY_3: String = FirebaseAnalytics.Param.ITEM_CATEGORY3

        /**
         * /The fourth category hierarchy or additional taxonomy for the item.
         *
         *
         * Name: item_category4
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: Crew
         */
        const val ITEM_CATEGORY_4: String = FirebaseAnalytics.Param.ITEM_CATEGORY4

        /**
         * The fifth category hierarchy or additional taxonomy for the item.
         *
         *
         * Name: item_category5
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: Short sleeve
         */
        const val ITEM_CATEGORY_5: String = FirebaseAnalytics.Param.ITEM_CATEGORY5

        /**
         * The ID of the list in which the item was presented to the user.
         *
         *
         * If set, event-level item_list_id is ignored.
         * If not set, event-level item_list_id is used, if present.
         *
         *
         * Name: item_list_id
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: related_products
         */
        const val ITEM_LIST_ID: String = FirebaseAnalytics.Param.ITEM_LIST_ID

        /**
         * The name of the list in which the item was presented to the user.
         *
         *
         * If set, event-level item_list_name is ignored.
         * If not set, event-level item_list_name is used, if present.
         *
         *
         * Name: item_list_name
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: Related products
         */
        const val ITEM_LIST_NAME: String = FirebaseAnalytics.Param.ITEM_LIST_NAME

        /**
         * The item variant or unique code or description for additional item details/options.
         *
         *
         * Name: item_variant
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: green
         */
        const val ITEM_VARIANT: String = FirebaseAnalytics.Param.ITEM_VARIANT

        /**
         * The location associated with the item. It's recommended to use the Google Place ID that corresponds to the associated item. A custom location ID can also be used.
         *
         *
         * If set, event-level location_id is ignored.
         * If not set, event-level location_id is used, if present.
         *
         *
         * Name: location_id
         *
         *
         * Type: string
         *
         *
         * Required: no
         *
         *
         * Example: L_12345
         */
        const val LOCATION_ID: String = FirebaseAnalytics.Param.LOCATION_ID

        /**
         * The monetary price of the item, in units of the specified currency parameter.
         *
         *
         * Name: price
         *
         *
         * Type: number
         *
         *
         * Required: no
         *
         *
         * Example: 5.85
         */
        const val PRICE: String = FirebaseAnalytics.Param.PRICE

        /**
         * Item quantity.
         *
         *
         * Name: quantity
         *
         *
         * Type: number
         *
         *
         * Required: no
         *
         *
         * Example: 1
         */
        const val QUANTITY: String = FirebaseAnalytics.Param.QUANTITY
    }

    // auto events
    object ScreenView {
        const val NAME: String = FirebaseAnalytics.Event.SCREEN_VIEW

        object Param {
            const val SCREEN_NAME: String = FirebaseAnalytics.Param.SCREEN_NAME
            const val SCREEN_CLASS: String = FirebaseAnalytics.Param.SCREEN_CLASS
        }
    }

    // recommended events
    object GenerateLead {
        /**
         * Log this event when a lead has been generated to understand the efficacy of your
         * re-engagement campaigns
         */
        const val NAME: String = FirebaseAnalytics.Event.GENERATE_LEAD

        object Param {
            /**
             * Currency of the items associated with the event, in 3-letter ISO 4217 format.
             *
             *
             * If set, item-level currency is ignored.
             * If not set, currency from the first item in items is used.
             *
             *
             * If you set value then currency is required for revenue metrics to be computed accurately.
             *
             *
             * Name: currency
             *
             *
             * Type: string
             *
             *
             * Required: yes (if value is set)
             *
             *
             * Example: USD
             */
            const val CURRENCY: String = FirebaseAnalytics.Param.CURRENCY

            /**
             * The monetary value of the event.
             *
             *
             * value is typically required for meaningful reporting.
             * If you mark the event as a conversion then it's recommended you set value.
             * currency is required if you set value.
             *
             *
             * Name: value
             *
             *
             * Type: number
             *
             *
             * Required: no (but recommended esp for conversions)
             *
             *
             * Example: 9.75
             */
            const val VALUE: String = FirebaseAnalytics.Param.VALUE
        }
    }

    object Purchase {
        object Param {
            /**
             * The method used to login (in case the purchase is also considered as a sign-up).
             *
             *
             * This is a Custom Param!
             *
             *
             * Name: method
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: Google
             */
            const val METHOD: String = FirebaseAnalytics.Param.METHOD

            /**
             * Currency of the items associated with the event, in 3-letter ISO 4217 format.
             *
             *
             * If set, item-level currency is ignored.
             * If not set, currency from the first item in items is used.
             *
             *
             * If you set value then currency is required for revenue metrics to be computed accurately.
             *
             *
             * Name: currency
             *
             *
             * Type: string
             *
             *
             * Required: yes (if value is set)
             *
             *
             * Example: USD
             */
            const val CURRENCY: String = FirebaseAnalytics.Param.CURRENCY

            /**
             * The monetary value of the event.
             *
             *
             * value is typically required for meaningful reporting.
             * If you mark the event as a conversion then it's recommended you set value.
             * currency is required if you set value.
             *
             *
             * Name: value
             *
             *
             * Type: number
             *
             *
             * Required: no (but recommended esp for conversions)
             *
             *
             * Example: 9.75
             */
            const val VALUE: String = FirebaseAnalytics.Param.VALUE

            /**
             * The unique identifier of a transaction.
             *
             *
             * Name: transaction_id
             *
             *
             * Type: string
             *
             *
             * Required: yes
             *
             *
             * Example: T_12345
             */
            const val TRANSACTION_ID: String = FirebaseAnalytics.Param.TRANSACTION_ID

            /**
             * A product affiliation to designate a supplying company or brick and mortar store location.
             *
             *
             * Event-level and item-level affiliation parameters are independent.
             *
             *
             * Name: affiliation
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: Google Store
             */
            const val AFFILIATION: String = FirebaseAnalytics.Param.AFFILIATION

            /**
             * The coupon name/code associated with the event.
             *
             *
             * Event-level and item-level coupon parameters are independent.
             *
             *
             * Name: coupon
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: SUMMER_FUN
             */
            const val COUPON: String = FirebaseAnalytics.Param.COUPON

            /**
             * Shipping cost associated with a transaction.
             *
             *
             * Name: shipping
             *
             *
             * Type: number
             *
             *
             * Required: no
             *
             *
             * Example: 4.35
             */
            const val SHIPPING: String = FirebaseAnalytics.Param.SHIPPING

            /**
             * Tax cost associated with a transaction.
             *
             *
             * Name: tax
             *
             *
             * Type: number
             *
             *
             * Required: no
             *
             *
             * Example: 1.35
             */
            const val TAX: String = FirebaseAnalytics.Param.TAX

            /**
             * The items for the event.
             *
             *
             * Name: items
             *
             *
             * Type: Item[]
             *
             *
             * Required: yes
             */
            const val ITEMS: String = FirebaseAnalytics.Param.ITEMS
        }

        /**
         * This event signifies when one or more items is purchased by a user.
         */
        const val NAME: String = FirebaseAnalytics.Event.PURCHASE
    }

    object SelectContent {
        object Param {
            /**
             * The type of selected content.
             *
             *
             * Name: content_type
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: product
             */
            const val CONTENT_TYPE: String = FirebaseAnalytics.Param.CONTENT_TYPE

            /**
             * An identifier for the item that was selected.
             *
             *
             * Name: item_id
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: I_12345
             */
            const val ITEM_ID: String = FirebaseAnalytics.Param.ITEM_ID
        }

        /**
         * This event signifies that a user has selected some content of a certain type.
         *
         *
         * This event can help you identify popular content and categories of content in your app.
         */
        const val NAME: String = FirebaseAnalytics.Event.SELECT_CONTENT
    }

    object SelectItem {
        object Param {
            /**
             * The ID of the list in which the item was presented to the user.
             *
             *
             * Ignored if set at the item-level.
             *
             *
             * Name: item_list_id
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: related_products
             */
            const val ITEM_LIST_ID: String = FirebaseAnalytics.Param.ITEM_LIST_ID

            /**
             * The name of the list in which the item was presented to the user.
             *
             *
             * Ignored if set at the item-level.
             *
             *
             * Name: item_list_name
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: Related products
             */
            const val ITEM_LIST_NAME: String = FirebaseAnalytics.Param.ITEM_LIST_NAME

            /**
             * The items for the event.
             *
             *
             * The items array is expected to have a single element, representing the selected item.
             *
             *
             * If multiple elements are provided, only the first element in items will be used.
             *
             *
             * Name: items
             *
             *
             * Type: Item[]
             *
             *
             * Required: yes
             */
            const val ITEMS: String = FirebaseAnalytics.Param.ITEMS
        }

        /**
         * This event signifies an item was selected from a list.
         */
        const val NAME: String = FirebaseAnalytics.Event.SELECT_ITEM
    }

    object ViewItem {
        object Param {
            /**
             * Currency of the items associated with the event, in 3-letter ISO 4217 format.
             *
             *
             * If set, item-level currency is ignored.
             * If not set, currency from the first item in items is used.
             *
             *
             * If you set value then currency is required for revenue metrics to be computed accurately.
             *
             *
             * Name: currency
             *
             *
             * Type: string
             *
             *
             * Required: yes (if value is set)
             *
             *
             * Example: USD
             */
            const val CURRENCY: String = FirebaseAnalytics.Param.CURRENCY

            /**
             * The monetary value of the event.
             *
             *
             * value is typically required for meaningful reporting. If you mark the event as a conversion then it's recommended you set value.
             *
             *
             * currency is required if you set value.
             *
             *
             * Name: value
             *
             *
             * Type: number
             *
             *
             * Required: no (but recommended esp for conversions)
             *
             *
             * Example: 7.77
             */
            const val VALUE: String = FirebaseAnalytics.Param.VALUE

            /**
             * The items for the event.
             *
             *
             * Name: items
             *
             *
             * Type: Item[]
             *
             *
             * Required: yes
             */
            const val ITEMS: String = FirebaseAnalytics.Param.ITEMS
        }

        /**
         * This event signifies that some content was shown to the user.
         *
         *
         * Use this event to discover the most popular items viewed.
         */
        const val NAME: String = FirebaseAnalytics.Event.VIEW_ITEM
    }

    object ViewItemList {
        object Param {
            /**
             * The ID of the list in which the item was presented to the user.
             *
             *
             * Ignored if set at the item-level.
             *
             *
             * Name: item_list_id
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: related_products
             */
            const val ITEM_LIST_ID: String = FirebaseAnalytics.Param.ITEM_LIST_ID

            /**
             * The name of the list in which the item was presented to the user.
             *
             *
             * Ignored if set at the item-level.
             *
             *
             * Name: item_list_name
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: Related products
             */
            const val ITEM_LIST_NAME: String = FirebaseAnalytics.Param.ITEM_LIST_NAME

            /**
             * The items for the event.
             *
             *
             * Name: items
             *
             *
             * Type: Item[]
             *
             *
             * Required: Yes
             */
            const val ITEMS: String = FirebaseAnalytics.Param.ITEMS
        }

        /**
         * Log this event when the user has been presented with a list of items of a certain category.
         */
        const val NAME: String = FirebaseAnalytics.Event.VIEW_ITEM_LIST
    }

    object Login {
        object Param {
            /**
             * /The method used to login.
             *
             *
             * Name: method
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: Google
             */
            const val METHOD: String = FirebaseAnalytics.Param.METHOD
        }

        /**
         * Send this event to signify that a user has logged in.
         */
        const val NAME: String = FirebaseAnalytics.Event.LOGIN
    }

    object SignUp {
        object Param {
            /**
             * The method used for sign up.
             *
             *
             * Name: method
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: Google
             */
            const val METHOD: String = FirebaseAnalytics.Param.METHOD
        }

        /**
         * This event indicates that a user has signed up for an account.
         *
         *
         * Use this event to understand the different behaviors of logged in and logged out users.
         */
        const val NAME: String = FirebaseAnalytics.Event.SIGN_UP
    }

    object JoinGroup {
        object Param {
            /**
             * The ID of the group.
             *
             *
             * Name: group_id
             *
             *
             * Type: string
             *
             *
             * Required: no
             *
             *
             * Example: G_12345
             */
            const val GROUP_ID: String = FirebaseAnalytics.Param.GROUP_ID
        }

        /**
         * Log this event when a user joins a group such as a guild, team, or family.
         * Use this event to analyze how popular certain groups or social features are.
         */
        const val NAME = "join_group"
    }

    // costume events
    object LocalFailure {
        object Param {
            const val TYPE = "FAILURE_TYPE"
            const val AUTO_HANDLED = "FAILURE_AUTO_HANDLED"
            const val PAYLOAD = "FAILURE_PAYLOAD"
        }

        const val NAME = "LOCAL_FAILURE"
    }

    object ContextInit {
        object Param {
            const val TIMESTAMP = "CONTEXT_INIT_TIMESTAMP"
        }

        const val NAME = "CONTEXT_INIT"
    }

    object WorkerRequest {
        object Param {
            const val ID = "WORKER_REQUEST_ID"
            const val TIMESTAMP = "WORKER_REQUEST_TIMESTAMP"
            const val WORKER_NAME = "WORKER_REQUEST_NAME"
            const val UPTIME = "WORKER_REQUEST_UPTIME"
        }

        const val NAME = "WORKER_REQUEST"
    }

    object WorkerSession {
        object Param {
            /**
             * number of millis between job enqueue and it's actual run time
             */
            const val DELAY = "WORKER_SESSION_DELAY"
            const val ID = "WORKER_SESSION_ID"
            const val WORKER_NAME = "WORKER_SESSION_NAME"
        }

        const val NAME = "WORKER_SESSION"
    }

    object ServiceMissingWarning {
        object Param {
            const val SOURCE_OPERATION = "SERVICE_MISSING_SOURCE_OPERATION"
        }

        const val NAME = "SERVICE_MISSING_WARNING"
    }

    object ActivationNeededWarning {
        object Param {
            const val SOURCE_OPERATION = "ACTIVATION_NEEDED_SOURCE_OPERATION"
        }

        const val NAME = "ACTIVATION_NEEDED_WARNING"
    }

    object SetTicket {
        object Param {
            const val TICKET_ID = "TICKET_ID" //not reported
            const val TICKET_NUMBER = "TICKET_NUMBER"
        }

        const val NAME = "SET_TICKET"
    }

    object ClearTicket {
        object Param {
            const val TICKET_ID = "TICKET_ID" //not reported
            const val TICKET_CLEARING_TRIGGER = "TICKET_CLEARING_TRIGGER"
        }

        const val NAME = "CLEAR_TICKET"
    }

    object SnoozeAlarm {
        const val NAME = "SNOOZE_ALARM"
    }

    object StartCamera {
        const val NAME = "START_CAMERA"
    }

    object ScanQR {
        const val NAME = "SCAN_QR"
    }

    object DenyPermission {
        object Param {
            const val DENIED_PERMISSION = "DENIED_PERMISSION"
        }

        const val NAME = "DENY_PERMISSION"
    }

    object HideCompactDashboard {
        object Param {
            const val COMPACT_UI_HIDING_TRIGGER = "COMPACT_UI_HIDING_TRIGGER"
        }

        const val NAME = "HIDE_COMPACT_DASHBOARD"
    }

    object ShowCompactDashboard {
        object Param {
            const val COMPACT_UI_SHOWING_TRIGGER = "COMPACT_UI_SHOWING_TRIGGER"
        }

        const val NAME = "SHOW_COMPACT_DASHBOARD"
    }

    object SmsSent {
        object Param {
            const val SMS_ID = "SENT_SMS_ID" //not reported
            const val SMS_TOKEN = "SENT_SMS_TOKEN" //not reported
        }

        const val NAME = "SMS_SENT"
    }

    object SmsNotSent {
        object Param {
            const val SMS_ID = "UNSENT_SMS_ID" //not reported
            const val SMS_TOKEN = "UNSENT_SMS_TOKEN" //not reported
            const val SMS_OUTCOME = "UNSENT_SMS_OUTCOME"
        }

        const val NAME = "SMS_NOT_SENT"
    }

    object SmsDelivered {
        object Param {
            const val SMS_ID = "DELIVERED_SMS_ID" //not reported
            const val SMS_TOKEN = "DELIVERED_SMS_TOKEN" //not reported
        }

        const val NAME = "SMS_DELIVERED"
    }

    object ActivationRejected {
        object Param {
            const val ACTIVATION_OUTCOME = "REJECTED_ACTIVATION_OUTCOME"
            const val ACTIVATION_DATE = "ACTIVATION_DATE"
        }

        const val NAME = "ACTIVATION_REJECTED"
    }

    object ShowDialog {
        object Param {
            const val DIALOG_NAME = "SHOWN_DIALOG_NAME"
        }

        const val NAME = "SHOW_DIALOG"
    }

    object HideDialog {
        object Param {
            const val DIALOG_NAME = "HIDDEN_DIALOG_NAME"
        }

        const val NAME = "HIDE_DIALOG"
    }

    object EditorSubmission {
        object Param {
            const val EDITOR_LABEL = "SUBMITTED_EDITOR_LABEL"
        }

        const val NAME = "EDITOR_SUBMISSION"
    }

    object ControllerClick {
        object Param {
            const val CONTROLLER_NAME = "CLICKED_CONTROLLER_NAME"
            const val CONTROLLER_PAYLOAD = "CLICKED_CONTROLLER_PAYLOAD"
        }

        const val NAME = "CONTROLLER_CLICK"
    }

    object NewTurnAlarmLimited {
        const val NAME = "NEW_TURN_ALARM_LIMITED"
    }

    object NewTurnAlarmExist {
        const val NAME = "NEW_TURN_ALARM_EXIST"
    }

    object NewTurnAlarm {
        object Param {
            const val NEW_VALUE = "NEW_ALARM_DURATION_IN_MINUTES"
        }

        const val NAME = "NEW_TURN_ALARM"
    }

    object DeleteTurnAlarmPhone {
        object Param {
            const val TURN_ALARM_ID = "DELETED_ALARM_ID" //not reported
        }

        const val NAME = "DELETE_TURN_ALARM_PHONE"
    }

    object UpdateTurnAlarmPhone {
        object Param {
            const val TURN_ALARM_ID = "PHONE_UPDATED_TURN_ALARM_ID" //not reported
            const val NEW_VALUE = "UPDATED_TURN_ALARM_PHONE"
        }

        const val NAME = "UPDATE_TURN_ALARM_PHONE"
    }

    object UpdateTurnAlarmVibrate {
        object Param {
            const val TURN_ALARM_ID = "VIBRATION_UPDATED_TURN_ALARM_ID" //not reported
            const val NEW_VALUE = "UPDATED_TURN_ALARM_VIBRATION"
        }

        const val NAME = "UPDATE_TURN_ALARM_VIBRATION"
    }

    object UpdateTurnAlarmSnooze {
        object Param {
            const val TURN_ALARM_ID = "SNOOZE_UPDATED_TURN_ALARM_ID" //not reported
            const val NEW_VALUE = "UPDATED_TURN_ALARM_SNOOZE"
        }

        const val NAME = "UPDATE_TURN_ALARM_SNOOZE"
    }

    object UpdateTurnAlarmRingtone {
        object Param {
            const val TURN_ALARM_ID = "RINGTONE_UPDATED_TURN_ALARM_ID" //not reported
            const val NEW_VALUE = "UPDATED_TURN_ALARM_RINGTONE"
        }

        const val NAME = "UPDATE_TURN_ALARM_RINGTONE"
    }

    object UpdateTurnAlarmPriority {
        object Param {
            const val TURN_ALARM_ID = "PRIORITY_UPDATED_TURN_ALARM_ID" //not reported
            const val NEW_VALUE = "UPDATED_TURN_ALARM_PRIORITY"
        }

        const val NAME = "UPDATE_TURN_ALARM_PRIORITY"
    }

    object UpdateTurnAlarmMinLiquidity {
        object Param {
            const val TURN_ALARM_ID = "LIQUIDITY_UPDATED_TURN_ALARM_ID" //not reported
            const val NEW_VALUE = "UPDATED_TURN_ALARM_LIQUIDITY"
        }

        const val NAME = "UPDATE_TURN_ALARM_MIN_LIQUIDITY"
    }

    object UpdateTurnAlarmMaxQueueLength {
        object Param {
            const val TURN_ALARM_ID =
                "MAX_QUEUE_LENGTH_UPDATED_TURN_ALARM_ID" //not reported
            const val NEW_VALUE = "UPDATED_TURN_ALARM_MAX_QUEUE_LENGTH"
        }

        const val NAME = "UPDATE_TURN_ALARM_MAX_QUEUE_LENGTH"
    }

    object UpdateTurnAlarmDuration {
        object Param {
            const val TURN_ALARM_ID = "DURATION_UPDATED_TURN_ALARM_ID" //not reported
            const val NEW_VALUE = "UPDATED_TURN_ALARM_DURATION"
        }

        const val NAME = "UPDATE_TURN_ALARM_DURATION"
    }

    object UpdateTurnAlarmEnabled {
        object Param {
            const val TURN_ALARM_ID = "ENABLED_UPDATED_TURN_ALARM_ID" //not reported
            const val NEW_VALUE = "UPDATED_TURN_ALARM_ENABLED"
        }

        const val NAME = "UPDATE_TURN_ALARM_ENABLED"
    }

    object SmsVerificationStateUpdated {
        object Param {
            const val UPDATED_SMS_VERIFICATION_STATE = "UPDATED_SMS_VERIFICATION_STATE"
        }

        const val NAME = "SMS_VERIFICATION_STATE_UPDATED"
    }

    object SmsVerificationCodeManualInput {
        object Param {
            const val INPUT_LENGTH = "SMS_VERIFICATION_CODE_INPUT_LENGTH"
        }

        const val NAME = "SMS_VERIFICATION_CODE_MANUAL_INPUT"
    }

    object SuccessfulActivationAppCheck {
        object Param {
            const val DURATION = "SUCCESSFUL_ACTIVATION_APPCHECK_DURATION"
        }

        const val NAME = "SUCCESSFUL_ACTIVATION_APPCHECK"
    }

    object FailedActivationAppCheck {
        object Param {
            const val DURATION = "FAILED_ACTIVATION_APPCHECK_DURATION"
        }

        const val NAME = "FAILED_ACTIVATION_APPCHECK"
    }

    object UnexpectedConditionTag {
        object Param {
            const val TAG = "UNEXPECTED_CONDITION_TAG"
            const val PAYLOAD = "UNEXPECTED_CONDITION_PAYLOAD"
        }

        const val NAME = "UNEXPECTED_CONDITION"
    }
}