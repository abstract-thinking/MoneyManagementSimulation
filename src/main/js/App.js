import React from 'react';
import ReactDOM from 'react-dom';
import { Router } from "@reach/router";
import RiskManagement from "./RiskManagement"
import SaleRecommendation from "./SaleRecommendation"

const App = () => {
  console.log("hello");
  return (
              <div>
                <Router>
                  <RiskManagement path="/" />
                  <SaleRecommendation path="riskManagements/:riskId/sell-recommendations/:investmentId/" />
                </Router>
              </div>

      );
};

ReactDOM.render(
    <App />,
    document.getElementById('react')
)

// export default App;
