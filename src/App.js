import React from 'react';
import { Router } from "@reach/router";
import RiskManagement from "./RiskManagement"
import SellRecommendation from "./SellRecommendation"

const App = () => {
  return (
              <div>
                <Router>
                  <RiskManagement path="/" />
                  <SellRecommendation path="/riskManagements/:riskId/sell-recommendations/:investmentId" />
                  <BuyRecommendation path="/riskManagements/:riskId/buy-recommendations/"
                </Router>
              </div>

      );
};

export default App;
