import React from "react";

const PurchaseRecommendation = ({
  purchaseRecommendation,
  showPurchaseButton
}) => (
  <tr className="purchase-rec-row">
    <td className="text-content">{purchaseRecommendation.wkn}</td>
    <td className="text-content">{purchaseRecommendation.name}</td>
    <td className="number-content">{purchaseRecommendation.rsl.toFixed(2)}</td>
    <td className="number-content">{purchaseRecommendation.quantity}</td>
    <td className="number-content">
      {purchaseRecommendation.price.toFixed(2)}
    </td>
    <td className="number-content">
      {purchaseRecommendation.notionalSalesPrice.toFixed(2)}
    </td>
    {showPurchaseButton && (
      <td>
        <button>+</button>
      </td>
    )}
  </tr>
);

export default PurchaseRecommendation;
