import React from 'react';
import ReactDOM from 'react-dom';
import { Router } from "@reach/router";
import RiskManagement from "./RiskManagement"
import SaleRecommendation from "./SaleRecommendation"
import PurchaseRecommendations from "./PurchaseRecommendations";

const App = () => {
    console.log("hello");

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

ReactDOM.render(
    <App />,
    document.getElementById('react')
)

export default App;
