(ns status-im.ui.screens.network-settings.events
  (:require [re-frame.core :refer [dispatch dispatch-sync after] :as re-frame]
            [status-im.utils.handlers :refer [register-handler] :as handlers]
            [status-im.utils.handlers :as u]
            [status-im.data-store.networks :as networks]
            [status-im.utils.js-resources :as js-res]
            [status-im.utils.types :as t]))

;;;; FX

(re-frame/reg-fx
  ::save-networks
  (fn [networks]
    (networks/save-all networks)))

;; handlers

(handlers/register-handler-fx
  :add-networks
  (fn [{:keys [networks] :as db} [_ new-networks]]
    (let [identities    (set (keys networks))
          new-networks' (->> new-networks
                             (remove #(identities (:id %)))
                             (map #(vector (:id %) %))
                             (into {}))]
      {:db            (-> db
                          (update :networks merge new-networks')
                          (assoc :new-networks (vals new-networks')))
       :save-networks new-networks'})))

(defn- generate-config [network-id data-dir]
  (t/clj->json {:NetworkId network-id :DataDir data-dir}))

(handlers/register-handler-fx
  :load-default-networks!
  (fn [db]
    (let [default-networks (mapv
                             (fn [[id {:keys [network-id name data-dir]}]]
                               {:id     (clojure.core/name id)
                                :name   name
                                :config (generate-config network-id data-dir)})
                             js-res/default-networks)
          new-networks'    (->> default-networks
                                (map #(vector (:id %) %))
                                (into {}))]
      {:db (assoc db :networks new-networks')})))

(handlers/register-handler-fx
  :connect-network
  (fn [_ [_ network]]
    {:dispatch-n [[:account-update {:network network}]
                  [:navigate-to-clean :accounts]]}))
