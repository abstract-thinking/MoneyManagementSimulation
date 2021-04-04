import React from "react";
import ReactDOM from "react-dom";
import { Router } from "@reach/router";
import RiskManagementChooser from "./RiskManagementChooser";
import RiskManagement from "./RiskManagement";
import SaleRecommendations from "./SalesRecommendations";
import SaleRecommendation from "./SaleRecommendation";
import PurchaseRecommendations from "./PurchaseRecommendations";
import RiskManagementModifier from "./RiskManagementModifier";
import CalculationExplanation from "./CalculationExplanation";

const App = () => {
  return (
    <>
      <Router>
        <RiskManagementChooser path="/" />
        <RiskManagement path="riskManagements/:riskManagementId" />
        <SaleRecommendations path="riskManagements/:riskId/sell-recommendations" />
        <SaleRecommendation path="riskManagements/:riskId/sell-recommendations/:investmentId" />
        <PurchaseRecommendations path="riskManagements/:riskId/purchase-recommendations" />
        <RiskManagementModifier path="riskManagements/:riskId/modifier" />
        <CalculationExplanation path="riskManagements/:riskId/calculation" />
      </Router>
    </>
  );
};

ReactDOM.render(<App />, document.getElementById("root"));

export default App;
