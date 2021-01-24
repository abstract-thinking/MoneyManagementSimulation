import React from 'react';
import { Router } from "@reach/router";
import PurchaseRecommendations from "./PurchaseRecommendations"
import RiskManagement from "./RiskManagement"
import SaleRecommendation from "./SaleRecommendation"

const App = () => {
  return (
              <div>
                <Router>
                  <RiskManagement path="/" />
                  <SaleRecommendation path="riskManagements/:riskId/sell-recommendations/:investmentId/" />
                  <PurchaseRecommendations path="riskManagements/:riskId/purchase-recommendations" />
                </Router>
              </div>

      );
};

export default App;
