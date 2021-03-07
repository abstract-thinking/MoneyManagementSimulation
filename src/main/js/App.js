import React from "react";
import ReactDOM from "react-dom";
import { Router } from "@reach/router";
import RiskManagement from "./RiskManagement";
import SaleRecommendations from "./SalesRecommendations";
import SaleRecommendation from "./SaleRecommendation";
import PurchaseRecommendations from "./PurchaseRecommendations";
import Search from "./Search";

const App = () => {
  return (
    <div>
      <Router>
        <RiskManagement path="/" />
        <SaleRecommendations path="riskManagements/:riskId/sell-recommendations" />
        <SaleRecommendation path="riskManagements/:riskId/sell-recommendations/:investmentId" />
        <PurchaseRecommendations path="riskManagements/:riskId/purchase-recommendations" />
        <Search path="/riskManagements/:riskId/search" />
      </Router>
    </div>
  );
};

ReactDOM.render(<App />, document.getElementById("react"));

export default App;
