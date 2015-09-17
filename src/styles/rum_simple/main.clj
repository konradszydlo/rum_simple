(ns styles.rum-simple.main
  (:require [garden.color :refer [rgb]]
            [garden.def :refer [defstyles]]
            [garden.units :refer [px]]))

(defstyles main
  [[:div {:margin-bottom (px 15)}]

   [:#services {:display :inline-block
                :width (px 340)
                :text-align :left
                :border [[(px 1) :solid  (rgb 97 161 188)]]}
    [:p {:display :block
          :padding [[(px 15) (px 20)]]
          :background-color (rgb 248 248 248)
          :color (rgb 123 133 133)
          :margin-bottom (px 3)
          :position :relative
         :cursor :pointer}
     [:&:hover {:background-color (rgb 216 242 241)}]
     [:&.active {:color (rgb 255 255 255)
                 :background-color (rgb 65 199 194)}
      [:b {:color (rgb 255 255 0)}]]
     [:b {:position :absolute
          :right (px 28)
          :line-height (px 16)
          :width (px 100)
          :color (rgb 128 135 135)
          :text-align :right}]]]
   [:#total {:padding-top (px 10)}]])
