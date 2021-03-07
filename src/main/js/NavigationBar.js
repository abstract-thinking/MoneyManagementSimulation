import React from "react";
import { navigate } from "@reach/router";

class NavigationBar extends React.Component {
  navigateToSalesRecommendation = () =>
    navigate(
      `/riskManagements/${this.props.riskManagementId}/sell-recommendations`
    );
  navigateToPurchaseRecommendation = () =>
    navigate(
      `/riskManagements/${this.props.riskManagementId}/purchase-recommendations`
    );

  navigateToSearch = () =>
    navigate(`/riskManagements/${this.props.riskManagementId}/search`);

  render() {
    return (
      <div className="navbar">
        <button onClick={this.navigateToSalesRecommendation}>
          Sales Recommendation
        </button>
        <button onClick={this.navigateToPurchaseRecommendation}>
          Purchase Recommendation
        </button>
      </div>
    );
  }
}

export default NavigationBar;
