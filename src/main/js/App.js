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
import Explanations from "./Explanations";

const App = () => {
  return (
    <>
      <Router>
        <RiskManagementChooser path="/" />
        <RiskManagement path="riskManagements/:riskManagementId" />
        <SaleRecommendations path="riskManagements/:riskManagementId/sell-recommendations" />
        <SaleRecommendation path="riskManagements/:riskManagementId/sell-recommendations/:investmentId" />
        <PurchaseRecommendations path="riskManagements/:riskManagementId/purchase-recommendations" />
        <RiskManagementModifier path="riskManagements/:riskManagementId/modifier" />
        <CalculationExplanation path="riskManagements/:riskManagementId/calculation" />
        <Explanations path="/riskManagements/:riskManagementId/current" />
      </Router>
    </>
  );
};

ReactDOM.render(<App />, document.getElementById("root"));

export default App;
