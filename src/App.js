import React from 'react';
import { Router } from "@reach/router";
import RiskManagement from "./RiskManagement"
import SellRecommendation from "./SellRecommendation"

const App = () => {
  return (
              <div>
                <Router>
                  <RiskManagement path="/" />
                  <SellRecommendation path="/risks/:riskId/sell-recommendations/:investmentId" />
                </Router>
              </div>

      );
};

export default App;
