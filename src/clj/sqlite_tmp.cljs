(ns sqlite-tmp)

;; (comment

;;   sqlite

;;   (def sdb (sqlite/openDatabase "sdb.db"))

;;   (-> sdb js-keys)
;;   (-> sdb (goog.object.get "transaction"))

;;   (.transaction
;;    sdb
;;    (fn [tx]
;;      (prn "in transaction")
;;      (prn tx)
;;      (def x tx)
;;      (.executeSql tx "create table if not exists items (id integer primary key not null, done int, value text);"
;;                   nil
;;                   (fn [& more]
;;                     (prn more)
;;                     (def xmore more)
;;                     (prn "sql success handler"))
;;                   (fn [& more]
;;                     (prn more)
;;                     (prn "sql failure handler"))))
;;    (fn [& more]
;;      (prn "success handler"))
;;    (fn [& err]
;;      (def xerr err)
;;      (prn "err handler")))

;;   ;; viem sa dostat k vysledku ale z nejakeho dovodu to konci jak err

;;   ;; som mesuge, ide to ok, posledny cb je success a nie error, tj mal som to naopak
;;   (def result-set (second xmore))
;;   (-> result-set js-keys)
;;   (-> result-set .-rows (goog.object.get "_array"))

;;   (-> x js-keys)



;;   ;; INSERT
;;   (.transaction
;;    sdb
;;    (fn [tx]
;;      (prn "in transaction")
;;      (prn tx)
;;      (def x tx)
;;      (.executeSql tx "insert into items (done, value) values (0, \"xi xi xi\");"
;;                   nil
;;                   (fn [& more]
;;                     (prn more)
;;                     (def xmore more)
;;                     (def result-set (second more))
;;                     (prn "sql success handler"))
;;                   (fn [& more]
;;                     (prn more)
;;                     (prn "sql failure handler"))))
;;    (fn [& more]
;;      (def xerr more)
;;      (prn "err handler"))
;;    (fn []
;;      (prn "success handler")))


;;   ;; SELECT

;;   (.transaction
;;    sdb
;;    (fn [tx]
;;      (prn "in transaction")
;;      (prn tx)
;;      (def x tx)
;;      (.executeSql tx "select * from items;"
;;                   nil
;;                   (fn [& more]
;;                     (prn more)
;;                     (def xmore more)
;;                     (def result-set (second more))
;;                     (prn "sql success handler"))
;;                   (fn [& more]
;;                     (prn more)
;;                     (prn "sql failure handler"))))
;;    (fn [& more]
;;      (def xerr more)
;;      (prn "err handler"))
;;    (fn []
;;      (prn "success handler")))

;;   (-> result-set .-rows (goog.object.get "_array")))

;; (comment

;;   ;; https://github.com/expo/examples/blob/master/with-sqlite/App.js

;;   ;; function openDatabase() {
;;   ;; if (Platform.OS === "web") {
;;   ;;   return {
;;   ;;     transaction: () => {
;;   ;;       return {
;;   ;;         executeSql: () => {},
;;   ;;       };
;;   ;;     },
;;   ;;   };
;;   ;; }



;;   (def sdb (sqlite/openDatabase "db.db"))
;;   (-> sdb js-keys)
;;   (-> sdb .-_db)


;;   ;; (.transaction sdb)
;;   ;; (.close sdb)

;;   (def result-create-table
;;     (.transaction
;;      sdb
;;      (fn [tx]
;;        (def tx-result tx)
;;        (.executeSql
;;         tx
;;         "create table if not exists items (id integer primary key not null, done int, value text);"))
;;      (fn [_] (prn "success"))
;;      (fn [& more] (def error-create-more)
;;        (prn "failure"))))

;;   (def insert-result
;;     (.transaction sdb
;;                   (fn [^js tx]
;;                     (.executeSql tx "insert into items (done, value) values (0, 'xi xi xi');"
;;                                  (fn [& more]
;;                                    (def insert-more more))))))

;;   (def select-result
;;     (.transaction
;;      sdb
;;      (fn [tx]
;;        (.executeSql tx "select * from items;" #js []
;;                     (fn [a b c] (def select-more b))))))

;;   (.close sdb)
;;   (js-keys sdb)
;;   (def x (.closeAsync sdb))
;;   (-> x
;;       (.then (fn [val] (def close-val val)))
;;       (.catch (fn [err] (def close-err err))))

;;   (declare bla)
;;   (-> (bla.resolve)
;;       (.then (fn [val] (def close-val val)))
;;       (.catch (fn [err] (def close-err err)))))