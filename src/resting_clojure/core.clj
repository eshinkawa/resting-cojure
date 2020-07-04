(ns resting-clojure.core
  (:require [org.httpkit.server :as server]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [clojure.pprint :as pp]
            [clojure.string :as str]
            [clojure.data.json :as json])
  (:gen-class))

(defn slurping [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    {:name     (str "Como estás " (:name (:params req)))
             :loveLife false}})

(def people-collection (atom []))

(defn get-people [req]
  {:status  200
   :headers {"Content-Type" "text/html"}
   :body    (str (json/write-str @people-collection))})

(defn get-parameter [req pname]
  (get [:params req] pname))

(defn add-person [firstname lastname]
  (swap! people-collection conj {:firstname (str/capitalize firstname) :lastname (str/capitalize lastname)}))

(defn adding-people [req]
  {:status  200
   :headers {"Content-Type" "text/json"}
   :body    (-> (let [p (partial get-parameter req)]
                  (pp/pprint (:params req))
                  (pp/pprint (p :lastname))
                  (str (json/write-str (add-person (:firstname (:params req)) (:lastname (:params req)))))))})

(defroutes app-routes
           (GET "/hello" [] slurping)
           (GET "/people" [] get-people)
           (GET "/add" [] adding-people))

(defn -main
  "This is our main entry point"
  [& args]
  (let [port (Integer/parseInt (or (System/getenv "PORT") "3000"))]
    ; Run the server with Ring.defaults middleware
    (server/run-server (wrap-defaults #'app-routes site-defaults) {:port port})
    ; Run the server without ring defaults
    ;(server/run-server #'app-routes {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))
