(ns smacksmacksmack.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.events :as events]
            [goog.dom :as gdom]
            [cljs.core.async :as async :refer [chan put! pipe unique merge map< filter< alts!]]
            [clojure.string :as string])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(def app-state (atom {:key "press any key" :color nil}))

(def colors ["#342254" "#996F9B" "#25233C" "#522541" "#404F76" "#FE58ED" "#382A66" "#083589"])

(def voices ["US English Female" "UK English Female" "UK English Male" "US English Male"])

(defn key-view [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "key"
                    :id (str "key-" (string/replace (:key app) #" " "-"))
                    :style #js {:color (:color app)}}
               (:key app)))))

(defn attach-root [element-id]
  (om/root key-view
           app-state
           {:target (. js/document (getElementById "content"))}))

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))

(defmulti get-key identity)

(defmethod get-key 220 [_]
  "back slash")

(defmethod get-key 191 [_]
  "slash")

(defmethod get-key 91 [_]
  "command")

(defmethod get-key 93 [_]
  "command")

(defmethod get-key 37 [_]
  "left")

(defmethod get-key 38 [_]
  "up")

(defmethod get-key 39 [_]
  "right")

(defmethod get-key 40 [_]
  "down")

(defmethod get-key 32 [_]
  "space")

(defmethod get-key 27 [_]
  "escape")

(defmethod get-key 18 [_]
  "alt")

(defmethod get-key 17 [_]
  "control")

(defmethod get-key 16 [_]
  "shift")

(defmethod get-key 13 [_]
  "enter")

(defmethod get-key 9 [_]
  "tab")

(defmethod get-key 8 [_]
  "delete")

(defmethod get-key :default [char-code]
  (.fromCharCode js/String char-code))

(defn random-voice []
  ;(get (rand-nth (js->clj (.getVoices js/responsiveVoice))) "name")
  (rand-nth voices))

(defn listen-for-keys []
  (let [keypresses (listen (gdom/getDocument) "keydown")]
    (go (while true
          (let [key-event (<! keypresses)
                char-code (.-keyCode key-event)
                key-pressed (get-key char-code)]
            (.log js/console (str "char code:" char-code))
            (.speak js/responsiveVoice key-pressed (random-voice))
            (swap! app-state assoc :key key-pressed :color (rand-nth colors)))))))

(defn main []
  (attach-root "content")
  (listen-for-keys))
