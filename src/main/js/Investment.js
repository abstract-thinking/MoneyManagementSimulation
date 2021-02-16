import React from 'react';
import { Link } from '@reach/router'

const Investment = ({riskManagementId, investment}) =>
    <tr className="investment-data">
     <td className="text-content">{investment.wkn}</td>
     <td className="text-content">{investment.name}</td>
     <td className="number-content">{investment.quantity}</td>
     <td className="number-content">{investment.purchasePrice.toFixed(2)}</td>
     <td className="number-content">{investment.investment.toFixed(2)}</td>
     <td className="number-content">{investment.notionalSalesPrice.toFixed(2)}</td>
     <td className="number-content">{investment.transactionCosts.toFixed(2)}</td>
     <td className="number-content">{investment.notionalRevenue.toFixed(2)}</td>
     <td className="number-content">{investment.positionRisk.toFixed(2)}</td>
     {
        investment._links && investment._links.sale ?
        <td><Link to={`/riskManagements/${riskManagementId}/sell-recommendations/${investment.id}`}>*</Link></td>
         : null
     }
    </tr>

export default Investment;