import React from 'react';
import { Router } from "@reach/router";
import RiskManagement from "./RiskManagement"
import SaleRecommendation from "./SaleRecommendation"

const App = () => {
  return (
              <div>
                <Router>
                  <RiskManagement path="/" />
                  <SaleRecommendation path="riskManagements/:riskId/sell-recommendations/:investmentId/" />
                </Router>
              </div>

      );
};

export default App;
