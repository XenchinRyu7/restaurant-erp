import React, { useState } from 'react';
import { History, Search, ArrowUpRight, ArrowDownRight, RefreshCw, Filter } from 'lucide-react';
import {
  Table,
  TableHeader,
  TableColumn,
  TableBody,
  TableRow,
  TableCell,
  Input,
  Chip,
  Select,
  SelectItem,
  Button
} from '@heroui/react';
import { InventoryTransaction } from '../types/erp';

interface TransactionsPageProps {
  transactions: InventoryTransaction[];
  onRefresh: () => void;
}

export const TransactionsPage: React.FC<TransactionsPageProps> = ({
  transactions,
  onRefresh,
}) => {
  const [search, setSearch] = useState('');
  const [selectedType, setSelectedType] = useState<string>('all');

  const filtered = transactions.filter((tx) => {
    const matchSearch =
      tx.productName.toLowerCase().includes(search.toLowerCase()) ||
      tx.productCode.toLowerCase().includes(search.toLowerCase()) ||
      (tx.referenceNumber && tx.referenceNumber.toLowerCase().includes(search.toLowerCase()));
    const matchType = selectedType === 'all' || tx.transactionType === selectedType;
    return matchSearch && matchType;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row items-stretch sm:items-center justify-between gap-4">
        <div className="flex items-center gap-3 flex-1 max-w-md">
          <Input
            placeholder="Search by product or reference..."
            size="sm"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            startContent={<Search className="w-4 h-4 text-neutral-400" />}
            isClearable
            onClear={() => setSearch('')}
          />

          <Select
            size="sm"
            label="Type"
            className="w-44"
            selectedKeys={[selectedType]}
            onChange={(e) => setSelectedType(e.target.value || 'all')}
          >
            <SelectItem key="all">All Movements</SelectItem>
            <SelectItem key="IN_PURCHASE">IN_PURCHASE</SelectItem>
            <SelectItem key="OUT_SALES">OUT_SALES</SelectItem>
            <SelectItem key="ADJUSTMENT">ADJUSTMENT</SelectItem>
          </Select>
        </div>

        <Button
          size="sm"
          variant="flat"
          onClick={onRefresh}
          startContent={<RefreshCw className="w-4 h-4" />}
        >
          Refresh Ledger
        </Button>
      </div>

      <div className="border border-neutral-200 dark:border-neutral-800 rounded-2xl bg-white dark:bg-neutral-900 shadow-sm overflow-hidden">
        <Table aria-label="Transactions Audit Table" shadow="none">
          <TableHeader>
            <TableColumn>TIMESTAMP</TableColumn>
            <TableColumn>PRODUCT</TableColumn>
            <TableColumn>WAREHOUSE</TableColumn>
            <TableColumn>TRANSACTION TYPE</TableColumn>
            <TableColumn>QUANTITY</TableColumn>
            <TableColumn>REFERENCE NUMBER</TableColumn>
            <TableColumn>NOTES / DETAILS</TableColumn>
          </TableHeader>
          <TableBody emptyContent="No stock movement records found.">
            {filtered.map((tx) => {
              const isPositive = Number(tx.quantity) > 0;
              return (
                <TableRow key={tx.id}>
                  <TableCell className="text-xs text-neutral-500 whitespace-nowrap">
                    {new Date(tx.createdAt).toLocaleString()}
                  </TableCell>
                  <TableCell>
                    <span className="font-semibold text-xs text-neutral-900 dark:text-neutral-100 block">
                      {tx.productName}
                    </span>
                    <span className="text-[10px] text-neutral-400 font-mono">{tx.productCode}</span>
                  </TableCell>
                  <TableCell className="text-xs text-neutral-700 dark:text-neutral-300">
                    {tx.warehouseName}
                  </TableCell>
                  <TableCell>
                    <Chip
                      size="sm"
                      variant="flat"
                      color={
                        tx.transactionType === 'IN_PURCHASE'
                          ? 'success'
                          : tx.transactionType === 'OUT_SALES'
                          ? 'primary'
                          : 'warning'
                      }
                      className="text-[10px] h-5 font-bold"
                    >
                      {tx.transactionType}
                    </Chip>
                  </TableCell>
                  <TableCell className="font-bold text-xs">
                    <span className={isPositive ? 'text-emerald-600' : 'text-rose-600'}>
                      {isPositive ? `+${tx.quantity}` : tx.quantity}
                    </span>
                  </TableCell>
                  <TableCell className="text-xs font-mono text-neutral-600 dark:text-neutral-400">
                    {tx.referenceNumber || '-'}
                  </TableCell>
                  <TableCell className="text-xs text-neutral-500 max-w-xs truncate">
                    {tx.notes || '-'}
                  </TableCell>
                </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </div>
    </div>
  );
};
