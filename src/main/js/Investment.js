import React from "react";
import { Link } from "@reach/router";
import { Button } from "react-bootstrap";

const Investment = ({ riskManagementId, investment }) => (
  <tr className="investment-data">
    <td className="text-content">{investment.symbol}</td>
    <td className="text-content">{investment.name}</td>
    <td className="number-content">{investment.quantity}</td>
    <td className="number-content">{investment.purchasePrice.toFixed(2)}</td>
    <td className="number-content">{investment.investment.toFixed(2)}</td>
    <td className="number-content">
      {investment.notionalSalesPrice.toFixed(2)}
    </td>
    <td className="number-content">{investment.transactionCosts.toFixed(2)}</td>
    <td className="number-content">{investment.notionalRevenue.toFixed(2)}</td>
    <td className="number-content">{investment.positionRisk.toFixed(2)}</td>
    <td>
      {investment._links && investment._links.sale ? (
        <Link to={`/riskManagements/${riskManagementId}/sell-recommendations/`}>
          <Button>
            <span className="badge bg-secondary">Verkaufen</span>
          </Button>
        </Link>
      ) : null}
    </td>
  </tr>
);

export default Investment;
