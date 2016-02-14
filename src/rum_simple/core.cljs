(ns ^:figwheel-always rum-simple.core
    (:require ; [rum :include-macros true]
             [rum] ))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

(rum/defc label [n text]
  [:.label (repeat n text)])

(rum/defcs stateful < (rum/local 0) [state title]
  (let [local (:rum/local state)]
    [:div
     {:on-click (fn [_] (swap! local inc))}
     title ": " @local]))

(def autorefresh-mixin {
  :did-mount (fn [state]
               (let [comp      (:rum/react-component state)
                     callback #(rum/request-render comp)
                     interval  (js/setInterval callback 1000)]
                 (assoc state ::interval interval)))
  :transfer-state (fn [old-state state]
                    (merge state (select-keys old-state [::interval])))
  :will-unmount (fn [state]
                  (js/clearInterval (::interval state)))})

(rum/defc timer < autorefresh-mixin []
  [:div.timer (.toISOString (js/Date.))])

;; times - reactive (reagent style)
(defn now []
  (.getTime (js/Date.)))

(defn el [id]
  (.getElementById  js/document id))

(defn ts->str [ts]
  (let [str (.toISOString (js/Date. ts))]
    (subs str 11 (dec (count str)))))

(defn make-red []
  "#FA8D97")

(defn make-green []
  "#0F0")

(defn make-blue []
  "#000099")

(defn make-purple []
  "#FF007F")

(defn make-violet []
  "#7F00FF")

(defn make-brown []
  "#331099")

(def clock (atom (now)))
(def color (atom make-red))
(def speed (atom 167))

(defn get-color []
  (let [seconds (.getSeconds (js/Date.))]
    (cond
     (< 50 seconds) (make-red)
     (< 40 seconds) (make-green)
     (< 30 seconds) (make-violet)
     (< 20 seconds) (make-purple)
     (< 10 seconds) (make-blue)
     :else (make-brown))))

(defn alter-color []
  (swap! color  get-color))

(defn tick []
  (reset! clock (now))
  ;(reset! color ( get-color))
  (alter-color)
  (js/setTimeout tick @speed))

(tick)

(rum/defc colored-clock < rum/static [time color]
  [:span {:style {:color color}} (ts->str time)])

(rum/defc reactive-timer < rum/reactive []
  [:div "Reactive: "
   (colored-clock (rum/react clock) (rum/react color))])

;; real-time search
;; http://tutorialzine.com/2014/07/5-practical-examples-for-learning-facebooks-react-framework/

(def search-string (atom ""))

(def libraries
  [{:name "Backbone.js" :url "http://documentcloud.github.io/backbone/"}
   {:name "AngularJS" :url "https://angularjs.org/"}
   {:name "jQuery" :url "http://jquery.com/"}
   {:name "Prototype" :url "http://www.prototypejs.org/"}
   {:name "React" :url "http://facebook.github.io/react/"}
   {:name "Ember" :url "http://emberjs.com/"}
   {:name "Express" :url "http://expressjs.com/"}
   {:name "Knockout" :url "http://knockoutjs.com/"}
   {:name "Dojo" :url "http://dojotoolkit.org/"}])

(defn filter-list [lst string]
  (filter #(re-find (re-pattern (str "(?i)" string)) (:name %)) lst))

(defn get-filtered-list [lst string]
  (if (> (count string) 0)
    (filter-list libraries string)
    lst))

(rum/defc li-item < rum/static [name url]
   [:li
    [:a {:href url :target "_blank"} name]])

(rum/defc search-list < rum/reactive [lst]
  (let [items (get-filtered-list lst (rum/react search-string))]
    [:ul
     (for [item items]
       (li-item (:name item) (:url item)))]))

(rum/defc search-input-box < rum/reactive [ref]
  [:input {:type "text"
           :value (rum/react ref)
           :on-change #(reset! ref (.. % -target -value)) }])

(rum/defc reactive-search < rum/reactive []
  [:div "Reactive search:"
   (search-input-box search-string)
   (search-list libraries)])

;; order-form
(def services
  [{:name "Web Development" :price 300}
   {:name "Design" :price 400}
   {:name "Integration" :price 250}
   {:name "Training" :price 220}])

(def order-form-total (atom 0))

(def order-form-color
  (atom make-red))

(rum/defc total-para < rum/reactive []
  [:p {:id "total"} (str "Total: £" (rum/react order-form-total))])

(defn toggle-class [old service]
  (let [price (:price service)]
    (if (= old "active")
      (do (swap! order-form-total - price)
          "")
      (do (swap! order-form-total + price)
          "active")))
  )

(rum/defcs make-service < (rum/local "") [state service]
  (let [local (:rum/local state)]
    [:p {:class @local
         :on-click (fn [_] (swap! local toggle-class service))}
     (:name service)
     [:b (str " £" (:price service))]
     ]))

(rum/defc service-chooser < rum/static []
  [:div
   [:h1 "Our Services"]
   [:div {:id "services"}
    (for [serv services]
      (make-service serv))
    (total-para)]])

(defn mount-components []
  (rum/mount (label 5 "abcd") (el "label-component"))
  (rum/mount (stateful "Clicks count") (el "click-counter"))
  (rum/mount (timer) (el "detailed-timer"))
  (rum/mount (reactive-timer) (el "reactive-timer"))
  (rum/mount (reactive-search) (el "library-search"))
  (rum/mount (service-chooser) (el "order-form")))

(defn on-js-reload []
  (mount-components))

(mount-components)
