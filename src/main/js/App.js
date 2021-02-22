import React from "react";
import ReactDOM from "react-dom";
import { Router } from "@reach/router";
import RiskManagement from "./RiskManagement";
import SaleRecommendations from "./SalesRecommendations";
import SaleRecommendation from "./SaleRecommendation";
import PurchaseRecommendations from "./PurchaseRecommendations";
import EditRiskManagement from "./EditRiskManagement";

const App = () => {
  return (
    <div>
      <Router>
        <RiskManagement path="/" />
        <SaleRecommendations path="riskManagements/:riskId/sell-recommendations" />
        <SaleRecommendation path="riskManagements/:riskId/sell-recommendations/:investmentId" />
        <PurchaseRecommendations path="riskManagements/:riskId/purchase-recommendations" />
        <EditRiskManagement path="/riskManagements/:riskId/edit" />
      </Router>
    </div>
  );
};

ReactDOM.render(<App />, document.getElementById("react"));

export default App;
