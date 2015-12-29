(ns smacksmacksmack.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [goog.events :as events]
            [goog.dom :as gdom]
            [cljs.core.async :as async :refer [chan put! pipe unique merge map< filter< alts!]])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(def app-state (atom {:keys []}))

(defn key-view [app owner]
  (reify
    om/IRender
    (render [_]
      (dom/div #js {:className "key"} (-> app :keys first)))))

(defn attach-root [element-id]
  (om/root key-view app-state {:target (. js/document (getElementById "content"))}))

(defn listen [el type]
  (let [out (chan)]
    (events/listen el type
                   (fn [e] (put! out e)))
    out))

(defn listen-for-keys []
  (let [keypresses (listen (gdom/getDocument) "keypress")]
    (go (while true
          (let [key-event (<! keypresses)
                char-code (.-charCode key-event)
                key-pressed (.fromCharCode js/String char-code)]
            (.log js/console (str "char code:" char-code))
            (swap! app-state assoc :keys [key-pressed]))))))

(defn main []
  (attach-root "content")
  (listen-for-keys))
