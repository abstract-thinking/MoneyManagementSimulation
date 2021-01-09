import React from 'react';
import { Link } from '@reach/router'

const Investment = ({investment}) =>
    <tr>
     <td>{investment.wkn}</td>
     <td>{investment.name}</td>
     <td>{investment.quantity}</td>
     <td>{investment.purchasePrice}</td>
     <td>{investment.investment}</td>
     <td>{investment.currentNotionalSalesPrice}</td>
     <td>{investment.transactionCosts}</td>
     <td>{investment.notionalRevenue}</td>
     <td>{investment.positionRisk}</td>
     {
        investment._links ? <td><Link to={investment._links.sell.href}>Click</Link></td> : null
     }
    </tr>

export default Investment;